package dev.ohhoonim.system.auditlog.model;

public interface MaskingPolicy {
    public MaskingResult apply(String rawJson);
}
