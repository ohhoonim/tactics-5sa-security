package dev.ohhoonim.system.auditlog.application;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import dev.ohhoonim.component.model.unit.Policy;
import dev.ohhoonim.system.auditlog.model.AuditLog;
import dev.ohhoonim.system.auditlog.model.AuditLogException;
import dev.ohhoonim.system.auditlog.model.IntegrityPolicy;

@Policy
public class HmacIntegrityPolicy implements IntegrityPolicy {
    private final String secretKey;
    private static final String ALGORITHM = "HmacSHA256";

    public HmacIntegrityPolicy(@Value("${audit.security.integrity-key}") String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String calculate(AuditLog log) {
        // 1. 해싱할 주요 비즈니스 필드들을 고정된 순서로 직렬화
        // 순서가 바뀌면 해시값이 달라지므로 '정렬된 규격'이 중요합니다.
        String payload = String.join("|", log.getId().toValue(), log.getOccurredAt().toString(),
                log.getActorId(), log.getTargetId(), log.getActionType(), log.getResultStatus(),
                log.getBeforeData(), log.getAfterData());

        return generateHmac(payload);
    }

    private String generateHmac(String payload) {
        try {
            SecretKeySpec secretKeySpec =
                    new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(secretKeySpec);
            byte[] hashBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new AuditLogException("무결성 해시 생성 중 기술적 오류가 발생했습니다.", e);
        }
    }

}
