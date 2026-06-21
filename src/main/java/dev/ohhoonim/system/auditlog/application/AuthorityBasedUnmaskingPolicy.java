package dev.ohhoonim.system.auditlog.application;

import dev.ohhoonim.component.model.unit.Policy;
import dev.ohhoonim.system.auditlog.model.EncryptionPolicy;
import dev.ohhoonim.system.auditlog.model.UnmaskingPolicy;

@Policy
public class AuthorityBasedUnmaskingPolicy implements UnmaskingPolicy {

    private final EncryptionPolicy encryptionPolicy; // 암호화 정책 주입

    public AuthorityBasedUnmaskingPolicy(EncryptionPolicy encryptionPolicy) {
        this.encryptionPolicy = encryptionPolicy;
    }

    @Override
    public String process(String rawData, UserRequester requester) {
        if (rawData == null || rawData.isBlank())
            return rawData;

        // 보안 책임자이면서 암호화된 패턴일 경우 복호화 수행
        if (requester.isSecurityOfficer() && isEncrypted(rawData)) {
            return encryptionPolicy.decrypt(rawData);
        }

        return rawData; // 일반 사용자는 암호화된 상태 그대로 노출 (또는 마스킹 처리)
    }

    private boolean isEncrypted(String data) {
        // 데이터가 특정 암호화 포맷(예: 특정 접두사)을 가졌는지 판단하는 로직
        return data.length() > 20; // 단순 예시
    }
}
