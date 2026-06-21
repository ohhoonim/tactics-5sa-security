package dev.ohhoonim.system.user.model;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import dev.ohhoonim.component.model.state.TransitionEvent;
import dev.ohhoonim.system.user.model.UserComponent.AccessSecurity;
import dev.ohhoonim.system.user.model.UserComponent.AllowedIpRange;
import dev.ohhoonim.system.user.model.UserComponent.AuthorityDelegation;
import dev.ohhoonim.system.user.model.UserComponent.LoginInfo;
import dev.ohhoonim.system.user.model.UserComponent.MenuAction;
import dev.ohhoonim.system.user.model.UserComponent.PasswordCredentials;
import dev.ohhoonim.system.user.model.UserComponent.UserAuthorization;
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

public sealed interface UserTransitionEvent extends TransitionEvent<User> permits LoginSuccess, HrPlacement, Invite, SignupRequest,
        VerifyIdentity, Approve, Terminate, LoginFailed, LockByAdmin, PasswordExpire, RoleChange,
        ExpirationOfDelgationPeriod, StepUpRequired, SeparationOfDuty, LongDaysOfInactivity,
        AcceptingDelegation, ChangedPassword, Unlocked, ResignedEmployee, AddAuthz {


    public record LoginSuccess() implements UserTransitionEvent  {

        @Override
        public List<UserPostAction> actions() {
            return List.of(u -> {
                u.setLoginInfo(new LoginInfo(null, Instant.now(), 0, null ));
            });
        }

    }

    /*
     * HR 배치 생성, 인사 정보는 있으나 본인 인증 전
     */
    public record HrPlacement() implements UserTransitionEvent {

        @Override
        public List<UserPostAction> actions() {
            return Collections.emptyList();
        }
    }

    /*
     * 관리자 초대
     */
    public record Invite() implements UserTransitionEvent {

        @Override
        public List<UserPostAction> actions() {
            return Collections.emptyList();
        }
    }

    /*
     * 사용자 가입 신청
     */
    public record SignupRequest() implements UserTransitionEvent {

        @Override
        public List<UserPostAction> actions() {
            return Collections.emptyList();
        }
    }

    /*
     * 본인 인증 완료
     */
    public record VerifyIdentity() implements UserTransitionEvent {

        @Override
        public List<UserPostAction> actions() {
            return Collections.emptyList();
        }
    }

    /*
     * 관리자 가입 승인
     */
    public record Approve() implements UserTransitionEvent {

        @Override
        public List<UserPostAction> actions() {
            return Collections.emptyList();
        }
    }

    /*
     * 퇴사 배치 / 관리자 차단
     */
    public record Terminate() implements UserTransitionEvent {

        @Override
        public List<UserPostAction> actions() {
            UserPostAction roleEmpty = u -> u.setAccessAction(MenuAction.empty());
            UserPostAction accessEmpty = u -> u.setAccessAction(MenuAction.empty());
            return List.of(roleEmpty, accessEmpty);
        }
    }

    /*
     * 로그인 5회 실패
     */
    public record LoginFailed(Integer userFaildCount) implements UserTransitionEvent {

        @Override
        public List<UserPostAction> actions() {
            return Collections.emptyList();
        }
    }

    /*
     * 관리자 본인확인 후 잠금 해제
     */
    public record LockByAdmin() implements UserTransitionEvent {
        @Override
        public List<UserPostAction> actions() {
            return Collections.emptyList();
        }
    }

    /*
     * 비밀번호 만료 확인
     */
    public record PasswordExpire(Instant lastChangedAt) implements UserTransitionEvent {
        @Override
        public List<UserPostAction> actions() {
            return Collections.emptyList();
        }
    }

    /*
     * 부서 이동
     */
    public record RoleChange() implements UserTransitionEvent {

        @Override
        public List<UserPostAction> actions() {
            // TODO effectivePermissions 변경
            UserPostAction action = u -> {
            };
            return List.of(action);
        }
    }

    /*
     * 직무 분리 위반 시도 (SoD) // 직무 분리(SoD, Separation of Duties) 위반 시도란, 시스템의 보안과 신뢰성을 해칠 수 있는 // 특정
     * 프로세스에서 한 사람에게 과도한 권한이 집중되거나, // 상호 견제해야 할 권한을 동시에 행사하려는 행위를 의미합니다.
     */
    public record SeparationOfDuty() implements UserTransitionEvent {
        @Override
        public List<UserPostAction> actions() {
            return Collections.emptyList();
        }
    }

    /*
     * 장기 미접속
     */
    public record LongDaysOfInactivity(Instant lastLoginAt) implements UserTransitionEvent {
        // 상태는 유지하되 권한만 최소화 (dormantAt 기록)
        @Override
        public List<UserPostAction> actions() {
            UserPostAction accessSecurityAction = u -> {
                var security = u.getSecurity();
                u.setSecurity(new AccessSecurity(security.allowedIpRanges(),
                        security.twoFactorSecret(), security.isTwoFactorEnabled(), Instant.now()));
            };
            return List.of(accessSecurityAction);
        }

    }

    /*
     * 권한 위임 수락
     */
    public record AcceptingDelegation(String delegatedUserId, Instant delegationExpiredAt,
            List<String> delegatedPermissions) implements UserTransitionEvent {
        // 본인 권한 + 위임 권한 행사 가능 상태
        @Override
        public List<UserPostAction> actions() {
            UserPostAction authorityDelegationAction = u -> u
                    .setDelegation(new AuthorityDelegation(delegatedUserId, delegationExpiredAt));
            UserPostAction userAuthorizationAction = u -> {
                var userAuth = u.getUserAuth();
                List<String> permissions = userAuth.effectivePermissions();
                List<String> newPermissions =
                        Stream.concat(permissions.stream(), delegatedPermissions.stream()).toList();

                u.setUserAuth(new UserAuthorization(userAuth.assignedRoles(), newPermissions,
                        userAuth.isHighPrivilege(), userAuth.isRoleSyncedWithHr()));
            };
            return List.of(authorityDelegationAction, userAuthorizationAction);
        }

    }

    /*
     * 위임 기한 만료 // 실행 시점과 비교 // 위임받은 권한 자동 회수
     */
    public record ExpirationOfDelgationPeriod(Instant delegationExpiredAt)
            implements UserTransitionEvent {
        @Override
        public List<UserPostAction> actions() {
            // TODO 위임 권한 제거 로직 필요 ??
            UserPostAction action = u -> {
            };
            return List.of(action);
        }
    }

    /*
     * 미승인 IP에서 고권한 시도
     */
    public record StepUpRequired(String clientIp, List<AllowedIpRange> range)
            implements UserTransitionEvent {
        // 행위 일시 정지 및 추가 인증(2FA) 요구
        @Override
        public List<UserPostAction> actions() {
            return Collections.emptyList();
        }
    }

    /*
     * 패스워드 변경
     */
    public record ChangedPassword() implements UserTransitionEvent {
        @Override
        public List<UserPostAction> actions() {
            UserPostAction passwordCredentialsAction = u -> {
                var passwordCredential = u.getPassword();
                u.setPassword(new PasswordCredentials(passwordCredential.password(), Instant.now(),
                        passwordCredential.temporaryPassword()));
            };
            return List.of(passwordCredentialsAction);
        }
    }

    /*
     * 계정 잠금 해제 // failedLoginAttempts 0으로 초기화
     */
    public record Unlocked() implements UserTransitionEvent {
        @Override
        public List<UserPostAction> actions() {
            UserPostAction loginInfoAction = u -> {
                var loginStats = u.getLoginInfo();
                u.setLoginInfo(new LoginInfo(loginStats.password(), loginStats.lastLoginAt(), 0,
                        loginStats.authSource()));
            };
            return List.of(loginInfoAction);
        }
    }

    /*
     * 퇴사자 처리 // 모든 Role 및 Permission 즉시 상실 ?? 상태만 바꾸는 것이 아니고?
     */
    public record ResignedEmployee() implements UserTransitionEvent {
        @Override
        public List<UserPostAction> actions() {
            return Collections.emptyList();
        }

    }

    /*
     * 권한 부여
     */
    public record AddAuthz(MenuAction addedAction) implements UserTransitionEvent {
        // assignedRoles 변경 → effectivePermissions 즉시 반영
        @Override
        public List<UserPostAction> actions() {
            UserPostAction addMenuAction = u -> {
                var accessAction = u.getAccessAction();
                List<String> menus = Stream.concat(accessAction.accesibleMenus().stream(), 
                    addedAction.accesibleMenus().stream()).toList();

                List<String> actions = Stream.concat(accessAction.allowedActions().stream(), 
                    addedAction.allowedActions().stream()).toList();

                u.setAccessAction(new MenuAction(menus, actions));
            };
            return List.of(addMenuAction);
        }
    }
}

