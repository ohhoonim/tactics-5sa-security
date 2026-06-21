package dev.ohhoonim.system.auditlog.application;

import dev.ohhoonim.component.model.payload.Dto;
import dev.ohhoonim.system.auditlog.model.AuditLogId;

/**
 * [UC-AUDIT-02] 무결성 검증 결과 리포트
 */
@Dto
public record IntegrityVerificationResult(
    AuditLogId logId,
    boolean isIntact,
    String status,
    String storedHash,
    String recalculatedHash
) {}