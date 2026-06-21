package dev.ohhoonim.system.auditlog.application;

import java.time.Instant;
import dev.ohhoonim.component.model.payload.Dto;

/**
 * [UC-AUDIT-01] 감사로그 기록 유스케이스를 위한 실행 명령 (Command)
 */
@Dto
public record AuditLogRecordCommand(
    Instant occurredAt,     // 실제 이벤트 발생 시각
    String clientIp,       // 요청 IP
    String userAgent,                // 브라우저/기기 정보
    String actorId,        // 행위자 식별자
    String targetId,       // 대상 식별자
    String targetType,     // 대상 유형 (USER, ROLE 등)
    String actionCategory, // 행위 카테고리 (AUTH, MEMBER_MGMT 등)
    String actionType,     // 행위 유형 (CREATE, UPDATE 등)
    String resultStatus,   // 결과 (SUCCESS, FAIL, DENIED)
    String beforeData,               // 변경 전 JSON (nullable)
    String afterData,                // 변경 후 JSON (nullable)
    String reason                    // 변경 사유
) {
    // 팩토리 메서드를 통해 유효성 검증이나 기본값 설정을 수행할 수 있습니다.
    public static AuditLogRecordCommand of(
            String clientIp, String actorId, 
            String targetId, String actionType, String resultStatus) {
        
        return new AuditLogRecordCommand(
            Instant.now(), clientIp, null, actorId, 
            targetId, "UNDEFINED", "GENERAL", actionType, resultStatus, 
            null, null, null
        );
    }
}
