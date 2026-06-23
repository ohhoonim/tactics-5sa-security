package dev.ohhoonim.system.auditlog.model;

import dev.ohhoonim.component.model.unit.DomainException;

public class AuditLogException extends DomainException{
    public AuditLogException(String message) {
        super(message);
    }

    public AuditLogException(String message, Throwable e) {
        super(message, e);
    }

    @Override
    public String errorCode() {
        return "AUDITLOG";
    }
}
