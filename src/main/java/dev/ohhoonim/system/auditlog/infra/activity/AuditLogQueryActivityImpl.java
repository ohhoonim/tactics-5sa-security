package dev.ohhoonim.system.auditlog.infra.activity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import dev.ohhoonim.component.model.paging.PageRequest;
import dev.ohhoonim.component.model.paging.PagedData;
import dev.ohhoonim.component.model.unit.Activity;
import dev.ohhoonim.system.auditlog.activity.AuditLogQueryActivity;
import dev.ohhoonim.system.auditlog.activity.out.AuditLogPostgresPort;
import dev.ohhoonim.system.auditlog.application.AuditLogSearchRequest;
import dev.ohhoonim.system.auditlog.model.AuditLog;
import dev.ohhoonim.system.auditlog.model.AuditLogId;

/**
 * [UC-AUDIT-02, UC-AUDIT-03] 
 */
@Activity
public class AuditLogQueryActivityImpl implements AuditLogQueryActivity  {

    private final AuditLogPostgresPort postgresPort;

    public AuditLogQueryActivityImpl(AuditLogPostgresPort postgresPort) {
        this.postgresPort = postgresPort;
    }

    @Override
    public Optional<AuditLog> findById(AuditLogId id) {
        return postgresPort.selectById(id.toValue());
    }

    @Override
    public List<AuditLog> findByActorAndPeriod(String actorId, Instant start, Instant end) {
        return postgresPort.selectByActorAndPeriod(actorId, start, end);
    }

    public List<AuditLog> findByJsonContent(String key, String value) {
        return postgresPort.selectByJsonContent(key, value);
    }

    @Override
    public List<AuditLogId> findIdsByDate(LocalDate endedDate) {
        return postgresPort.findIdsByData(endedDate);
    }

    @Override
    public PagedData<AuditLog> findByTarget(String targetType, String targetId,
            AuditLogSearchRequest searchRequest, PageRequest pageRequest) {
        return postgresPort.findByTarget(targetType, targetId, searchRequest, pageRequest);
    }

    @Override
    public PagedData<AuditLog> findByActor(String actorId, AuditLogSearchRequest searchRequest,
            PageRequest pageRequest) {
        return postgresPort.findByActor(actorId, searchRequest, pageRequest); 
    }
    
}
