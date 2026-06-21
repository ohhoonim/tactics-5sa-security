package dev.ohhoonim.system.auditlog.model;

public interface IntegrityPolicy {

    String calculate(AuditLog auditLog);

}
