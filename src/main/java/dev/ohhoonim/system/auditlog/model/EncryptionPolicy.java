package dev.ohhoonim.system.auditlog.model;

/**
 * [Step 2] 데이터 보호 정책 (가역적 암호화) 민감 정보를 안전하게 보관하고 필요 시 복호화하기 위한 법전입니다.
 */
public interface EncryptionPolicy {
    /**
     * 평문을 암호화합니다.
     */
    String encrypt(String plainText);

    /**
     * 암호문을 평문으로 복호화합니다.
     */
    String decrypt(String cipherText);
}
