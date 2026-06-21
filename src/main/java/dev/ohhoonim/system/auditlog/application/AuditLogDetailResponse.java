package dev.ohhoonim.system.auditlog.application;

import java.util.List;
import dev.ohhoonim.component.model.payload.Dto;
import dev.ohhoonim.system.auditlog.model.AuditLog;
import dev.ohhoonim.system.auditlog.model.AuditLogId;

/**
 * [UC-AUDIT-03] 감사로그 상세 응답 도메인 자아(AR)의 상태를 외부 인터페이스 규격으로 사상합니다.
 */
@Dto
public record AuditLogDetailResponse(AuditLogId logId, String occurrenceTime, // 포맷팅된 시각
        String actorInfo, // 행위자 정보 결합
        String actionDescription, // 카테고리 + 타입 결합
        String status, String beforeSnapshot, // 권한에 따라 가공된 데이터
        String afterSnapshot, // 권한에 따라 가공된 데이터
        String integrityStatus, // 무결성 상태 (별도 검증 결과가 있을 경우)
        List<String> maskedFields // 마스킹 처리되었던 필드 목록
) {
    /**
     * [Morphism] 도메인 모델과 가공된 데이터를 응답 객체로 변환합니다.
     */
    public static AuditLogDetailResponse from(AuditLog auditLog, String processedBefore,
            String processedAfter) {

        return new AuditLogDetailResponse(auditLog.getId(),
                auditLog.getOccurredAt().toString(),
                String.format("%s ", auditLog.getActorId()),
                String.format("[%s] %s", auditLog.getActionCategory(), auditLog.getActionType()),
                auditLog.getResultStatus(), processedBefore, processedAfter, "VERIFIED", 
                auditLog.getMaskedFieldsList() // AR 내부의 CSV를 리스트로 파싱한 결과
        );
    }
}
