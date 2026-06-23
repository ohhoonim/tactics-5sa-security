package dev.ohhoonim.component.model.unit;

public abstract class DomainException extends RuntimeException {
    public abstract String errorCode();

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable e) {
        super(message, e);
    }
}
