package dev.ohhoonim.system.auditlog.activity;

import dev.ohhoonim.system.auditlog.model.AuditLog;

/**
 * [UC-AUDIT-01] 감사로그 수집 및 기록
 * 유스케이스로부터 도출된 명령 액티비티
 */
public interface AuditLogCommandActivity {
    // 완성된 자아(AR)를 영속화하는 도구
    void save(AuditLog auditLog);
}
