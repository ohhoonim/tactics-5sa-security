package dev.ohhoonim.system.auditlog.application;

import java.time.LocalDate;
import dev.ohhoonim.component.model.payload.Dto;

/**
 * 감사 로그 조회를 위한 검색 파라미터 객체
 */
@Dto
public record AuditLogSearchRequest(
    LocalDate startDate,    // 검색 시작일
    LocalDate endDate,      // 검색 종료일
    String actionCategory,  // 도메인 분류 (예: AUTH, ACCOUNT)
    String actionType,      // 상세 행위 (예: LOGIN, CREATE)
    String resultStatus,    // 결과 상태 (SUCCESS, FAIL)
    String actorId,         // 수행자 ID
    String targetId,        // 대상 ID
    String targetType       // 대상 유형 (예: MEMBER, ORDER)
) {
    // 기본 생성자에서 날짜 범위 유효성 검사 등 추가 가능
    public AuditLogSearchRequest {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작일은 종료일보다 빨라야 합니다.");
        }
    }
}
