package dev.ohhoonim.system.auditlog.application;

import java.time.Instant;
import dev.ohhoonim.component.model.payload.Dto;
import dev.ohhoonim.system.auditlog.model.AuditLog;
import dev.ohhoonim.system.auditlog.model.AuditLogId;

/**
 * 감사 로그 목록 조회 시 반환되는 요약 응답 객체
 */
@Dto
public record AuditLogResponse(
    AuditLogId logId,
    Instant occurredAt,
    String actorId,
    String targetId,
    String targetType,
    String actionCategory,
    String actionType,
    String resultStatus,
    String reason,
    String clientIp
) {
    /**
     * 도메인 엔티티(AuditLog)로부터 Response DTO 변환
     */
    public static AuditLogResponse from(AuditLog log) {
        return new AuditLogResponse(
            log.getId(),
            log.getOccurredAt(),
            log.getActorId(),
            log.getTargetId(),
            log.getTargetType(),
            log.getActionCategory(),
            log.getActionType(),
            log.getResultStatus(),
            log.getReason(),
            log.getClientIp()
        );
    }
}
