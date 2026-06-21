package dev.ohhoonim.system.auditlog.activity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import dev.ohhoonim.component.model.paging.PageRequest;
import dev.ohhoonim.component.model.paging.PagedData;
import dev.ohhoonim.system.auditlog.application.AuditLogSearchRequest;
import dev.ohhoonim.system.auditlog.model.AuditLog;
import dev.ohhoonim.system.auditlog.model.AuditLogId;

/**
 * [UC-AUDIT-02, UC-AUDIT-03] 
 */
public interface AuditLogQueryActivity {
    Optional<AuditLog> findById(AuditLogId id);

    List<AuditLog> findByActorAndPeriod(String actorId, Instant start, Instant end);

    List<AuditLogId> findIdsByDate(LocalDate endedDate);

    PagedData<AuditLog> findByTarget(String targetType, String targetId,
            AuditLogSearchRequest searchRequest, PageRequest pageRequest);

    PagedData<AuditLog> findByActor(String actorId, AuditLogSearchRequest searchRequest,
            PageRequest pageRequest);
}
