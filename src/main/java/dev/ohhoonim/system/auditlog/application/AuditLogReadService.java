package dev.ohhoonim.system.auditlog.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.ohhoonim.component.model.paging.PageRequest;
import dev.ohhoonim.component.model.paging.PagedData;
import dev.ohhoonim.system.auditlog.activity.AuditLogQueryActivity;
import dev.ohhoonim.system.auditlog.model.AuditLog;
import dev.ohhoonim.system.auditlog.model.AuditLogException;
import dev.ohhoonim.system.auditlog.model.AuditLogId;
import dev.ohhoonim.system.auditlog.model.UnmaskingPolicy;

/**
 * 저장된 감사 로그를 조회하고 사용자에게 제공하는 서비스입니다.
 * <p>
 * 주요 특징:
 * <ul>
 * <li>과거 시점에 기록된 감사 객체(Aggregate Root)를 원본 그대로 복원합니다.</li>
 * <li>{@link UnmaskingPolicy}를 활용하여, 요청자({@link UserRequester})의 권한(Role)에 따라 마스킹된 데이터의 복원 여부를
 * 결정합니다.</li>
 * <li>보안 가이드라인에 따라 권한이 없는 사용자가 상세 정보를 조회하는 것을 방지합니다.</li>
 * </ul>
 * </p>
 */
@Service
public class AuditLogReadService {

    private final AuditLogQueryActivity queryActivity;
    private final UnmaskingPolicy unmaskingPolicy;

    

    public AuditLogReadService(AuditLogQueryActivity queryActivity,
            UnmaskingPolicy unmaskingPolicy) {
        this.queryActivity = queryActivity;
        this.unmaskingPolicy = unmaskingPolicy;
    }

    @Transactional(readOnly = true)
    public AuditLogDetailResponse getDetail(AuditLogId id, UserRequester requester) {
        AuditLog auditLog = queryActivity.findById(id)
                .orElseThrow(() -> new AuditLogException("로그를 찾을 수 없습니다."));
        return applyUnmasking(auditLog, requester);
    }

    private AuditLogDetailResponse applyUnmasking(AuditLog auditLog, UserRequester requester) {
        String processedBefore = unmaskingPolicy.process(auditLog.getBeforeData(), requester);
        String processedAfter = unmaskingPolicy.process(auditLog.getAfterData(), requester);
        return AuditLogDetailResponse.from(auditLog, processedBefore, processedAfter);
    }

    @Transactional(readOnly = true)
    public PagedData<AuditLogResponse> getLogsByTarget(String targetType, String targetId,
            AuditLogSearchRequest searchRequest, // 기간 필터 포함
            PageRequest pageRequest) {
        var targets = queryActivity.findByTarget(targetType, targetId, searchRequest, pageRequest);
        var auditLogResponses = targets.contents().stream().map(AuditLogResponse::from).toList();
        return new PagedData<>(auditLogResponses, targets.paged());
    }

    @Transactional(readOnly = true)
    public PagedData<AuditLogResponse> getLogsByActor(String actorId,
            AuditLogSearchRequest searchRequest, PageRequest pageRequest) {
        var actors = queryActivity.findByActor(actorId, searchRequest, pageRequest);
        var auditLogResponses = actors.contents().stream().map(AuditLogResponse::from).toList();
        return new PagedData<>(auditLogResponses, actors.paged());
    }

}
