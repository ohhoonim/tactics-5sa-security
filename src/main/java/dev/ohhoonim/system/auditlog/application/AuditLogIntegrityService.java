package dev.ohhoonim.system.auditlog.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.ohhoonim.system.auditlog.activity.AuditLogQueryActivity;
import dev.ohhoonim.system.auditlog.model.AuditLog;
import dev.ohhoonim.system.auditlog.model.AuditLogException;
import dev.ohhoonim.system.auditlog.model.AuditLogId;
import dev.ohhoonim.system.auditlog.model.IntegrityPolicy;

/**
 * 기록된 감사 로그의 무결성을 검증하여 위변조 여부를 판단하는 서비스입니다.
 * <p>
 * 검증 매커니즘:
 * <ul>
 * <li>DB에 저장된 당시의 해시값(Integrity Hash)과 현재 데이터 기반으로 재계산된 해시값을 비교합니다.</li>
 * <li>데이터의 단 1비트라도 수정될 경우 {@code TAMPERED} 상태를 반환하여 보안 사고를 탐지합니다.</li>
 * <li>검증 실패 시, 저장된 해시와 계산된 해시 정보를 제공하여 원인 분석을 지원합니다.</li>
 * </ul>
 * </p>
 */
@Service
public class AuditLogIntegrityService {

    private final AuditLogQueryActivity queryActivity;
    private final IntegrityPolicy integrityPolicy;

    
    public AuditLogIntegrityService(AuditLogQueryActivity queryActivity,
            IntegrityPolicy integrityPolicy) {
        this.queryActivity = queryActivity;
        this.integrityPolicy = integrityPolicy;
    }


    /**
     * [UC-AUDIT-02] 특정 감사로그의 무결성을 검증합니다.
     * 
     * @param id 검증할 로그의 식별자
     * @return 검증 결과 (SUCCESS: 일치, TAMPERED: 변조됨)
     */
    @Transactional(readOnly = true)
    public IntegrityVerificationResult verify(AuditLogId id) {
        // 1. [Activity] 과거의 자아를 복원 (reconstitute)
        AuditLog auditLog = queryActivity.findById(id).orElseThrow(
                () -> new AuditLogException("존재하지 않는 감사로그입니다. id: " + id.value()));

        boolean isIntact = auditLog.verifyIntegrity(integrityPolicy);

        // 3. 결과 반환 (상세 정보 포함)
        return new IntegrityVerificationResult(id, isIntact, isIntact ? "SUCCESS" : "TAMPERED",
                auditLog.getIntegrityHash(), integrityPolicy.calculate(auditLog));
    }
}
