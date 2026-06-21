package dev.ohhoonim.system.user.model;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import dev.ohhoonim.component.model.state.Status;
import dev.ohhoonim.system.user.model.UserComponent.AllowedIpRange;
import dev.ohhoonim.system.user.model.UserStatus.Actived;
import dev.ohhoonim.system.user.model.UserStatus.Actived.Active;
import dev.ohhoonim.system.user.model.UserStatus.Actived.Delegated;
import dev.ohhoonim.system.user.model.UserStatus.Actived.Deny;
import dev.ohhoonim.system.user.model.UserStatus.Actived.Restricted;
import dev.ohhoonim.system.user.model.UserStatus.Actived.StepUpReq;
import dev.ohhoonim.system.user.model.UserStatus.Invited;
import dev.ohhoonim.system.user.model.UserStatus.Locked;
import dev.ohhoonim.system.user.model.UserStatus.None;
import dev.ohhoonim.system.user.model.UserStatus.PasswordExpired;
import dev.ohhoonim.system.user.model.UserStatus.Pending;
import dev.ohhoonim.system.user.model.UserStatus.WaitingAproval;
import dev.ohhoonim.system.user.model.UserStatus.Withdrawn;
import dev.ohhoonim.system.user.model.UserTransitionEvent.AcceptingDelegation;
import dev.ohhoonim.system.user.model.UserTransitionEvent.AddAuthz;
import dev.ohhoonim.system.user.model.UserTransitionEvent.Approve;
import dev.ohhoonim.system.user.model.UserTransitionEvent.ChangedPassword;
import dev.ohhoonim.system.user.model.UserTransitionEvent.ExpirationOfDelgationPeriod;
import dev.ohhoonim.system.user.model.UserTransitionEvent.HrPlacement;
import dev.ohhoonim.system.user.model.UserTransitionEvent.Invite;
import dev.ohhoonim.system.user.model.UserTransitionEvent.LockByAdmin;
import dev.ohhoonim.system.user.model.UserTransitionEvent.LoginFailed;
import dev.ohhoonim.system.user.model.UserTransitionEvent.LoginSuccess;
import dev.ohhoonim.system.user.model.UserTransitionEvent.LongDaysOfInactivity;
import dev.ohhoonim.system.user.model.UserTransitionEvent.PasswordExpire;
import dev.ohhoonim.system.user.model.UserTransitionEvent.ResignedEmployee;
import dev.ohhoonim.system.user.model.UserTransitionEvent.RoleChange;
import dev.ohhoonim.system.user.model.UserTransitionEvent.SeparationOfDuty;
import dev.ohhoonim.system.user.model.UserTransitionEvent.SignupRequest;
import dev.ohhoonim.system.user.model.UserTransitionEvent.StepUpRequired;
import dev.ohhoonim.system.user.model.UserTransitionEvent.Terminate;
import dev.ohhoonim.system.user.model.UserTransitionEvent.Unlocked;
import dev.ohhoonim.system.user.model.UserTransitionEvent.VerifyIdentity;

public sealed interface UserStatus extends Status<UserStatus, UserTransitionEvent, User> permits
        None, Pending, Invited, Actived, Locked, Withdrawn, WaitingAproval, PasswordExpired {

    public static UserStatus fromString(String status) {
        return switch(status) {
            case "NONE" -> new None();
            case "PENDING" -> new Pending();
            case "INVITED" -> new Invited(); 
            case "ACTIVE" -> new Active();  // TODO Actived 관련 상태 추가 필요
            case "LOCKED" -> new Locked();
            case "WITHDRAWN" -> new Withdrawn();
            case "WAITINGAPROVAL" -> new WaitingAproval();
            case "PASSWORDEXPIRED" -> new PasswordExpired();
            default -> throw new UserException("처리할 수 없는 상태입니다.");
        };
    }
    /*
        상태 없음
    */
    public record None() implements UserStatus {
        @Override
        public UserTransitionResult trigger(UserTransitionEvent event) {
            return switch (event) {
                case LoginSuccess e -> new UserTransitionResult(new Active(), e.actions());
                case HrPlacement e -> new UserTransitionResult(new Pending(), e.actions());
                case Invite e -> new UserTransitionResult(new Invited(), e.actions());
                case SignupRequest e -> new UserTransitionResult(new WaitingAproval(), e.actions());
                default -> throw new UserException("상태변경이 불가능한 이벤트 입니다.");
            };
        }
    }

    /*
        대기   
    */
    public record Pending() implements UserStatus {
        @Override
        public UserTransitionResult trigger(UserTransitionEvent event) {
            return switch (event) {
                case VerifyIdentity e -> new UserTransitionResult(new Active(), e.actions());
                default -> throw new UserException("상태변경이 불가능한 이벤트 입니다.");
            };
        }
    }

    /*
        관리자 초대
    */
    public record Invited() implements UserStatus {
        @Override
        public UserTransitionResult trigger(UserTransitionEvent event) {
            return switch (event) {
                case VerifyIdentity e -> new UserTransitionResult(new Active(), e.actions());
                default -> throw new UserException("상태변경이 불가능한 이벤트 입니다.");
            };
        }
    }

    // 활성
    public sealed interface Actived extends UserStatus
            permits Active, Deny, Restricted, Delegated, StepUpReq {

        public record Active() implements Actived {
            @Override
            public UserTransitionResult trigger(UserTransitionEvent event) {
                return switch (event) {
                    case LoginSuccess e -> new UserTransitionResult(new Active(), e.actions());
                    case Terminate e -> new UserTransitionResult(new Withdrawn(), e.actions()); // 퇴사배치 / 관리자 차단
                    case LoginFailed e when e.userFaildCount() > 4 -> new UserTransitionResult(
                            new Locked(), e.actions()); // // 로그인 5회 실패
                    case PasswordExpire e when isExpired(e.lastChangedAt()) -> new UserTransitionResult(
                            new PasswordExpired(), e.actions());// 비밀번호 만료 확인
                    case RoleChange e -> new UserTransitionResult(new Active(), e.actions()); // 부서 이동 (HR 배치)
                    case SeparationOfDuty e -> new UserTransitionResult(new Deny(), e.actions()); //직무 분리 위반 시도
                    case LongDaysOfInactivity e when isInActivity(
                            e.lastLoginAt()) -> new UserTransitionResult(new Restricted(), e.actions()); // 장기 미접속 
                    case AcceptingDelegation e when isExpiredDelegate(
                            e.delegationExpiredAt()) -> new UserTransitionResult(new Delegated(),
                                    e.actions()); //권한 위임 수락
                    case ExpirationOfDelgationPeriod e -> new UserTransitionResult(new Active(),
                            e.actions()); // 위임 기한 만료
                    case StepUpRequired e when isSatisfiedBy(e.clientIp(),
                            e.range()) -> new UserTransitionResult(new StepUpReq(), e.actions()); // 미승인 IP에서 고권한 시도
                    case ResignedEmployee e -> new UserTransitionResult(new Withdrawn(), e.actions()); // 퇴사자 처리
                    case AddAuthz e -> new UserTransitionResult(new Active(), e.actions()); // 권한 부여 
                    default -> throw new UserException("상태변경이 불가능한 이벤트 입니다.");
                };
            }

            private Boolean isExpired(Instant lastChangedAt) {
                return lastChangedAt.atOffset(ZoneOffset.UTC).plusDays(90)
                        .isAfter(Instant.now().atOffset(ZoneOffset.UTC));
            }

            private Boolean isInActivity(Instant lastLoginAt) {
                return lastLoginAt.atOffset(ZoneOffset.UTC).plusYears(1)
                        .isAfter(Instant.now().atOffset(ZoneOffset.UTC));
            }

            private Boolean isExpiredDelegate(Instant delegationExpiredAt) {
                return delegationExpiredAt.atOffset(ZoneOffset.UTC)
                        .isAfter(Instant.now().atOffset(ZoneOffset.UTC));
            }

            private Boolean isSatisfiedBy(String clientIp, List<AllowedIpRange> range) {
                return range.stream().anyMatch(allowed -> !allowed.isSatisfiedBy(clientIp));
            }
        }
        public record Deny() implements Actived {

            @Override
            public UserTransitionResult trigger(UserTransitionEvent event) {
                throw new UserException("상태변경이 불가능한 이벤트 입니다.");
            }
        }

        public record Restricted() implements Actived {

            @Override
            public UserTransitionResult trigger(UserTransitionEvent event) {
                throw new UserException("상태변경이 불가능한 이벤트 입니다.");
            }
        }

        public record Delegated() implements Actived {

            @Override
            public UserTransitionResult trigger(UserTransitionEvent event) {
                throw new UserException("상태변경이 불가능한 이벤트 입니다.");
            }
        }

        public record StepUpReq() implements Actived {

            @Override
            public UserTransitionResult trigger(UserTransitionEvent event) {
                throw new UserException("상태변경이 불가능한 이벤트 입니다.");
            }
        }
    }

    public record Locked() implements UserStatus {
        // 관리자 본인확인 후 잠금 해제
        // 계정 잠금 해제
        @Override
        public UserTransitionResult trigger(UserTransitionEvent event) {
            return switch (event) {
                case LockByAdmin e -> new UserTransitionResult(new Active(), e.actions());
                case Unlocked e -> new UserTransitionResult(new Active(), e.actions());
                default -> throw new UserException("상태변경이 불가능한 이벤트 입니다");
            };
        }
    } // 잠금

    /*
        탈퇴 
    */
    public record Withdrawn() implements UserStatus {
        @Override
        public UserTransitionResult trigger(UserTransitionEvent event) {
            throw new UserException("상태변경이 불가능한 이벤트 입니다.");
        }
    }

    /*
        승인 대기  
    */
    public record WaitingAproval() implements UserStatus {
        @Override
        public UserTransitionResult trigger(UserTransitionEvent event) {
            return switch (event) {
                case Approve e -> new UserTransitionResult(new Active(), e.actions());
                default -> throw new UserException("상태변경이 불가능한 이벤트 입니다");
            };
        }

    }

    /*
        패스워드 만료
    */
    public record PasswordExpired() implements UserStatus {
        @Override
        public UserTransitionResult trigger(UserTransitionEvent event) {
            return switch (event) {
                case ChangedPassword e -> new UserTransitionResult(new Active(),
                        e.actions());
                default -> throw new UserException("상태변경이 불가능한 이벤트 입니다");
            };
        }

    }
}
