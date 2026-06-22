package dev.ohhoonim.system.user.model;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import dev.ohhoonim.system.user.model.UserComponent.AccessSecurity;
import dev.ohhoonim.system.user.model.UserComponent.AllowedIpRange;
import dev.ohhoonim.system.user.model.UserComponent.AuthSourceCode;
import dev.ohhoonim.system.user.model.UserComponent.AuthorityDelegation;
import dev.ohhoonim.system.user.model.UserComponent.LoginInfo;
import dev.ohhoonim.system.user.model.UserComponent.MenuAction;
import dev.ohhoonim.system.user.model.UserComponent.PasswordCredentials;
import dev.ohhoonim.system.user.model.UserComponent.UserAuthorization;
import dev.ohhoonim.system.user.model.UserComponent.UserProfile;

public sealed interface UserComponent permits UserProfile, LoginInfo, PasswordCredentials,
        UserAuthorization, AccessSecurity, AllowedIpRange, AuthorityDelegation, MenuAction, AuthSourceCode {
    
    // 기본 정보
    public record UserProfile(String username, // 사용자명(로그인 아이디로 사용)
            String employeeNo, // 사번
            String email, // 이메일
            UUID departmentId, // 부서 ID
            String jobPosition, // 직급
            String jobRole // 직무
    ) implements UserComponent {

    }
    // 로그인 정보
    public record LoginInfo(String password, // 패스워드
            Instant lastLoginAt, // 최근 로그인 시각
            Integer failedLoginAttempt, // 로그인 실패 시도 횟수
            AuthSourceCode authSource // 배치로 등록되었는지 직접 가입했는지 구분
    ) implements UserComponent {

    }

    public enum AuthSourceCode implements UserComponent {
        HR_SYSTEM,
        MANUAL, 
        SSO;

        public static AuthSourceCode fromString(String value) {
            if (value == null) {
                return null;
            }
            return AuthSourceCode.valueOf(value.toUpperCase());
        }
    }

    // 계정 자격 증명 그룹 (Credentials)
    public record PasswordCredentials(String password, // (LoginInfo에서 이동)
            Instant passwordChangedAt, // 마지막 변경일
            boolean temporaryPassword // 임시 비번 여부
    ) implements UserComponent {
    }

    // 권한 및 인가 그룹 (Authorization)
    public record UserAuthorization(List<String> assignedRoles, // 부여된 역할
            List<String> effectivePermissions, // 최종 계산된 권한
            boolean isHighPrivilege, // 민감 정보 접근 권한 여부
            boolean isRoleSyncedWithHr // HR 정보 동기화 여부
    ) implements UserComponent {
        public UserAuthorization {
            assignedRoles = List.copyOf(Objects.requireNonNullElse(assignedRoles, List.of()));
            effectivePermissions =
                    List.copyOf(Objects.requireNonNullElse(effectivePermissions, List.of()));
        }
    }

    // 보안 정책 및 인증 그룹 (SecurityPolicy)
    public record AccessSecurity(List<AllowedIpRange> allowedIpRanges, // 허용 IP 대역
            String twoFactorSecret, // 2FA 비밀키
            boolean isTwoFactorEnabled, // 2FA 활성화 여부
            Instant dormantAt // 휴면 시작 시점
    ) implements UserComponent {
        public AccessSecurity {
            allowedIpRanges = List.copyOf(Objects.requireNonNullElse(allowedIpRanges, List.of()));
        }
    }

    public record AllowedIpRange(String cidr) implements UserComponent {

        public boolean isSatisfiedBy(String clientIp) {
            try {
                String[] parts = cidr.split("/");
                InetAddress baseAddress = InetAddress.getByName(parts[0]);
                int prefixLength = Integer.parseInt(parts[1]);

                InetAddress targetAddress = InetAddress.getByName(clientIp);

                return isInRange(baseAddress, targetAddress, prefixLength);
            } catch (UnknownHostException | ArrayIndexOutOfBoundsException
                    | NumberFormatException e) {
                return false;
            }
        }

        private boolean isInRange(InetAddress base, InetAddress target, int prefix) {
            byte[] baseBytes = base.getAddress();
            byte[] targetBytes = target.getAddress();

            if (baseBytes.length != targetBytes.length)
                return false;

            int byteCount = prefix / 8;
            int bitCount = prefix % 8;

            for (int i = 0; i < byteCount; i++) {
                if (baseBytes[i] != targetBytes[i])
                    return false;
            }

            if (bitCount > 0) {
                int mask = (0xFF00 >> bitCount) & 0xFF;
                return (baseBytes[byteCount] & mask) == (targetBytes[byteCount] & mask);
            }

            return true;
        }
    }

    // 권한 위임 그룹 (Delegation)
    public record AuthorityDelegation(String delegatedFromUserId, // 위임자 ID
            Instant expiredAt // 위임 만료 일시
    ) implements UserComponent {
        public boolean isExpired() {
            return expiredAt != null && expiredAt.isBefore(Instant.now());
        }
    }

    // 메뉴 access
    public record MenuAction(List<String> accesibleMenus, // assignedRoles를 통해 계산된 접근 가능 메뉴 리스트
            List<String> allowedActions // 현재 메뉴 맥락에서 실행 가능한 버튼(permission) 리스트
    ) implements UserComponent {
        public MenuAction {
            accesibleMenus = List.copyOf(Objects.requireNonNullElse(accesibleMenus, List.of()));
            allowedActions = List.copyOf(Objects.requireNonNullElse(allowedActions, List.of()));
        }

        public static MenuAction empty() {
            return new MenuAction(Collections.emptyList(), Collections.emptyList());
        }
    }
}
