package dev.ohhoonim.system.auditlog.infra.activity;

import dev.ohhoonim.component.model.unit.Activity;
import dev.ohhoonim.system.auditlog.activity.AuditLogCommandActivity;
import dev.ohhoonim.system.auditlog.activity.out.AuditLogPostgresPort;
import dev.ohhoonim.system.auditlog.model.AuditLog;

/**
 * [UC-AUDIT-01] 감사로그 수집 및 기록 Activity 구현체: 유스케이스의 목적을 달성하기 위해 기술적 도구(Port)를 조합합니다.
 */
@Activity
public class AuditLogCommandActivityImpl implements AuditLogCommandActivity {

    private final AuditLogPostgresPort postgresPort;

    public AuditLogCommandActivityImpl(AuditLogPostgresPort postgresPort) {
        this.postgresPort = postgresPort;
    }

    @Override
    public void save(AuditLog auditLog) {
        postgresPort.insert(auditLog);
    }

}
