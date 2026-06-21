package dev.ohhoonim.system.auditlog.activity.out;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import dev.ohhoonim.component.model.paging.PagedData;
import dev.ohhoonim.component.model.paging.PageRequest;
import dev.ohhoonim.system.auditlog.application.AuditLogSearchRequest;
import dev.ohhoonim.system.auditlog.model.AuditLog;
import dev.ohhoonim.system.auditlog.model.AuditLogId;

public interface AuditLogPostgresPort {
    void insert(AuditLog auditLog);

    Optional<AuditLog> selectById(String id);

    List<AuditLog> findByTargetId(String targetId);

    List<AuditLog> selectByActorAndPeriod(String actorId, Instant start, Instant end);

    List<AuditLog> selectByJsonContent(String key, String value);

    List<AuditLogId> findIdsByData(LocalDate endedDate);

    PagedData<AuditLog> findByTarget(String targetType, String targetId,
                    AuditLogSearchRequest searchRequest, PageRequest pageRequest);

    PagedData<AuditLog> findByActor(String actorId, AuditLogSearchRequest searchRequest,
                    PageRequest pageRequest);
}
