package dev.ohhoonim.system.security.model;

import dev.ohhoonim.component.model.unit.DomainException;

public class SecurityAuthenticationException extends DomainException {

    private final BearerTokenErrorCode errorCode;

    public SecurityAuthenticationException(BearerTokenErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public SecurityAuthenticationException(BearerTokenErrorCode errorCode, Throwable e) {
        super(errorCode.getMessage(), e);
        this.errorCode = errorCode;
    }

    @Override
    public String errorCode() {
        return this.errorCode.getCode();
    }
}
