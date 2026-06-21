package dev.ohhoonim.system.auditlog.model;

import dev.ohhoonim.component.model.unit.DomainException;

@DomainException
public class AuditLogException extends RuntimeException {
    public AuditLogException(String message) {
        super(message);
    }

    public AuditLogException(String message, Throwable e) {
        super(message, e);
    }
}
