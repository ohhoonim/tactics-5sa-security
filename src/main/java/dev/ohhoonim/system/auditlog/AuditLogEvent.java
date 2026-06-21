package dev.ohhoonim.system.auditlog;

import java.time.Instant;

/**
 * 감사 로그 기록을 위한 전용 도메인 이벤트입니다.
 * 외부 모듈(Member, Order 등)에서 발행하며, 감사 모듈이 이를 구독합니다.
 */
public record AuditLogEvent(
    Instant occurredAt,
    String clientIp,
    String userAgent,
    String actorId,
    String targetId,
    String targetType,     // 예: MEMBER, PAY, ORDER
    String actionCategory, // 예: AUTH, ACCOUNT, SYSTEM
    String actionType,     // 예: CREATE, LOGIN, DELETE
    String resultStatus,   // SUCCESS, FAIL
    String beforeData,               // JSON String
    String afterData,                // JSON String
    String reason
) {
    // 팩토리 메서드를 통해 가독성 높은 이벤트 생성 지원
    public static AuditLogEvent of(String actorId, String targetId, String type, String action, String result) {
        return new AuditLogEvent(
            Instant.now(), "0.0.0.0", "unknown",
            actorId, targetId, type, "BUSINESS", action, result,
            null, null, null
        );
    }
}