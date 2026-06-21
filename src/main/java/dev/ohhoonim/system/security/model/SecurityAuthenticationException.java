package dev.ohhoonim.system.security.model;

import dev.ohhoonim.component.model.unit.DomainException;

@DomainException
public class SecurityAuthenticationException extends RuntimeException {
    public SecurityAuthenticationException(String message) {
        super(message);
    }

    public SecurityAuthenticationException(String message, Throwable e) {
        super(message, e);
    }
}
