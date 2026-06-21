package dev.ohhoonim.system.auditlog.model;

import java.util.List;
import dev.ohhoonim.component.model.unit.ValueObject;

/**
 * [Step 2] 마스킹 정책 집행 결과
 * 마스킹된 데이터와 추적을 위한 메타데이터를 포함합니다.
 */
@ValueObject
public record MaskingResult(
    String maskedJson,           // 마스킹 처리가 완료된 JSON 문자열
    List<String> maskedFields    // 마스킹 처리된 필드 이름 목록 (예: "password", "ssn")
) {
    /**
     * null 데이터나 빈 데이터에 대한 널 객체(Null Object) 패턴 적용
     */
    public static MaskingResult empty(String original) {
        return new MaskingResult(original, List.of());
    }

    /**
     * 마스킹된 필드가 있는지 여부 반환
     */
    public boolean hasMaskedFields() {
        return maskedFields != null && !maskedFields.isEmpty();
    }

    /**
     * AuditLog 모델의 maskedFields 필드에 저장하기 적합한 포맷(CSV)으로 변환
     */
    public String getMaskedFieldsAsCsv() {
        if (!hasMaskedFields()) return "";
        return String.join(",", maskedFields);
    }
}
