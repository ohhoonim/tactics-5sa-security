package dev.ohhoonim.system.auditlog.application;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.ohhoonim.system.auditlog.activity.AuditLogCommandActivity;
import dev.ohhoonim.system.auditlog.model.AuditLog;
import dev.ohhoonim.system.auditlog.model.AuditLogId;
import dev.ohhoonim.system.auditlog.model.IntegrityPolicy;
import dev.ohhoonim.system.auditlog.model.MaskingPolicy;
import dev.ohhoonim.system.auditlog.model.MaskingResult;

/**
 * 시스템 내 발생하는 주요 행위를 기록하고 관리하는 서비스입니다.
 * <p>
 * 이 서비스는 기록 요청(Command)을 받아 다음과 같은 절차를 수행합니다:
 * <ul>
 * <li>{@link MaskingPolicy}를 적용하여 개인정보 및 민감 데이터를 마스킹 처리합니다.</li>
 * <li>마스킹된 필드 목록을 추출하고 정렬하여 추적성을 확보합니다.</li>
 * <li>{@link IntegrityPolicy}를 통해 데이터의 무결성을 보장하는 해시 봉인을 수행합니다.</li>
 * </ul>
 * </p>
 */
@Service
public class AuditLogService {

    private final AuditLogCommandActivity commandActivity;
    private final MaskingPolicy maskingPolicy;
    private final IntegrityPolicy integrityPolicy;

    
    public AuditLogService(AuditLogCommandActivity commandActivity, MaskingPolicy maskingPolicy,
            IntegrityPolicy integrityPolicy) {
        this.commandActivity = commandActivity;
        this.maskingPolicy = maskingPolicy;
        this.integrityPolicy = integrityPolicy;
    }

    /**
     * [UC-AUDIT-01] 감사로그 수집 및 기록
     */
    @Transactional
    public void record(AuditLogRecordCommand command) {
        // 1. 전처리 (Masking Policy 적용)
        var beforeResult = maskingPolicy.apply(command.beforeData());
        var afterResult = maskingPolicy.apply(command.afterData());

        // 2. AR Creation (신규 자아 확립)
        AuditLog auditLog = new AuditLog(AuditLogId.Creator.generate(), command.actorId(),
                command.occurredAt(), command.clientIp(), command.userAgent(), command.actorId(),
                command.targetId(), command.targetType(), command.actionCategory(),
                command.actionType(), command.resultStatus(), beforeResult.maskedJson(),
                afterResult.maskedJson(), command.reason(),
                combineMaskedFields(beforeResult, afterResult));

        // 3. 비즈니스 수행 (Policy 주입 및 무결성 봉인)
        auditLog.seal(integrityPolicy);

        // 4. 결과 저장 명세 정의 및 실행
        commandActivity.save(auditLog);
    }

    /**
     * [UC-AUDIT-01] 전/후 데이터에서 마스킹된 필드 목록을 중복 없이 합칩니다.
     */
    private String combineMaskedFields(MaskingResult before, MaskingResult after) {
        // 1. 전/후 결과에서 마스킹된 필드 리스트 추출
        List<String> beforeFields = before.maskedFields();
        List<String> afterFields = after.maskedFields();

        // 2. Stream을 이용한 합치기, 중복 제거, 정렬 (추적성 향상)
        return Stream.concat(beforeFields.stream(), afterFields.stream()).filter(Objects::nonNull)
                .distinct() // 중복 제거 (예: password가 양쪽 다 있으면 한 번만 기록)
                .sorted() // 정렬하여 일관된 로그 포맷 유지
                .collect(Collectors.joining(",")); // CSV 형태로 결합
    }
}
