package dev.ohhoonim.system.auditlog.application;

import java.time.LocalDateTime;
import java.util.Set;
import dev.ohhoonim.component.model.payload.Dto;

/**
 * [Step 2] 요청자 보안 문맥
 * 정책(Policy) 집행 및 상세 조회(UC-AUDIT-03) 시 권한 판단의 근거로 사용됩니다.
 */
@Dto
public record UserRequester(
    String userId,            // 요청자 식별 ID
    String username,          // 요청자 이름
    Set<String> roles,        // 보유 권한 목록 (예: ROLE_ADMIN, ROLE_SECURITY)
    String requestIp,         // 요청지 IP
    LocalDateTime requestedAt // 요청 시각
) {
    /**
     * 특정 권한 보유 여부를 확인합니다.
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    /**
     * 보안 책임자(Security Officer) 권한이 있는지 확인합니다.
     * UC-AUDIT-03에서 마스킹 해제 여부를 결정하는 주요 기준이 됩니다.
     */
    public boolean isSecurityOfficer() {
        return hasRole("ROLE_SECURITY_OFFICER");
    }

    /**
     * 시스템 관리자 권한 여부를 확인합니다.
     */
    public boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }
}
