package dev.ohhoonim.system.auditlog.model;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import dev.ohhoonim.component.model.unit.BaseEntity;

public class AuditLog extends BaseEntity<AuditLogId> {
    // Context & Actors
    private final Instant occurredAt;
    private final String clientIp;
    private final String userAgent;
    private final String actorId;
    private final String targetId;
    private final String targetType;

    // Action Details
    private final String actionCategory;
    private final String actionType;
    private final String resultStatus;

    // Data Snapshot
    private final String beforeData;
    private final String afterData;
    private final String reason;

    // Security
    private String integrityHash; // 초기 생성 후 seal 단계에서 결정됨
    private final String maskedFields;

    // [Creation] 신규 로그 생성용 생성자
    public AuditLog(AuditLogId id, String operator, Instant occurredAt, String clientIp,
            String userAgent, String actorId, String targetId, String targetType,
            String actionCategory, String actionType, String resultStatus, String beforeData,
            String afterData, String reason, String maskedFields) {
        super(id, operator);
        this.occurredAt = occurredAt;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
        this.actorId = actorId;
        this.targetId = targetId;
        this.targetType = targetType;
        this.actionCategory = actionCategory;
        this.actionType = actionType;
        this.resultStatus = resultStatus;
        this.beforeData = beforeData;
        this.afterData = afterData;
        this.reason = reason;
        this.maskedFields = maskedFields;
    }

    // [Reconstitution] DB 복원용 정적 팩토리 메서드
    public static AuditLog reconstitute(AuditLogId id, Instant createdAt, String createdBy,
            Instant modifiedAt, String modifiedBy, Instant occurredAt, String clientIp,
            String userAgent, String actorId, String targetId, String targetType,
            String actionCategory, String actionType, String resultStatus, String beforeData,
            String afterData, String reason, String integrityHash, String maskedFields) {
        AuditLog log = new AuditLog(id, createdBy, occurredAt, clientIp, userAgent, actorId,
                targetId, targetType, actionCategory, actionType, resultStatus, beforeData,
                afterData, reason, maskedFields);

        log.integrityHash = integrityHash;
        return log;
    }

    public void seal(IntegrityPolicy policy) {
        if (this.integrityHash != null) {
            throw new AuditLogException("이미 무결성 봉인이 완료된 로그입니다.");
        }
        this.integrityHash = policy.calculate(this);
    }

    // AuditLog.java 내부
    public boolean verifyIntegrity(IntegrityPolicy policy) {
        if (this.integrityHash == null) {
            return false;
        }
        return this.integrityHash.equals(policy.calculate(this));
    }

    public String getIntegrityHash() {
        return this.integrityHash;
    }

    public Instant getOccurredAt() {
        return this.occurredAt;
    }

    public String getActorId() {
        return this.actorId;
    }

    public String getTargetId() {
        return this.targetId;
    }

    public String getActionType() {
        return this.actionType;
    }

    public String getResultStatus() {
        return this.resultStatus;
    }

    public String getBeforeData() {
        return this.beforeData;
    }

    public String getAfterData() {
        return afterData;
    }

    public String getClientIp() {
        return clientIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getTargetType() {
        return targetType;
    }

    public String getActionCategory() {
        return actionCategory;
    }

    public String getReason() {
        return reason;
    }

    public String getMaskedFields() {
        return maskedFields;
    }

    /**
     * DB에 저장된 CSV 형태의 마스킹 필드 목록을 리스트로 변환하여 반환합니다.
     * 이 메서드는 도메인 모델이 외부(Response 등)에 자신의 상태를 
     * 비즈니스에 적합한 형태로 사상(Mapping)하여 제공하는 역할을 합니다.
     */
    public List<String> getMaskedFieldsList() {
        if (this.maskedFields == null || this.maskedFields.isBlank()) {
            return List.of();
        }
        
        // CSV를 분리하고 각 요소의 공백을 제거하여 리스트로 변환
        return Arrays.stream(this.maskedFields.split(","))
                     .map(String::trim)
                     .filter(field -> !field.isEmpty())
                     .toList();
    }


}
