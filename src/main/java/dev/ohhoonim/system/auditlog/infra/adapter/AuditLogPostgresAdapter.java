package dev.ohhoonim.system.auditlog.infra.adapter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import dev.ohhoonim.component.model.paging.PageRequest;
import dev.ohhoonim.component.model.paging.Paged;
import dev.ohhoonim.component.model.paging.PagedData;
import dev.ohhoonim.component.model.unit.Adapter;
import dev.ohhoonim.system.auditlog.activity.out.AuditLogPostgresPort;
import dev.ohhoonim.system.auditlog.application.AuditLogSearchRequest;
import dev.ohhoonim.system.auditlog.model.AuditLog;
import dev.ohhoonim.system.auditlog.model.AuditLogId;

@Adapter
public class AuditLogPostgresAdapter implements AuditLogPostgresPort {

    private final JdbcClient jdbcClient;
    

    public AuditLogPostgresAdapter(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void insert(AuditLog auditLog) {
        String sql = """
                INSERT INTO system_audit_logs (
                     log_id, actor_id, target_id, target_type,
                     action_category, action_type, result_status,
                     occurred_at, client_ip, user_agent,
                     before_data, after_data, reason,
                     integrity_hash, masked_fields,
                     created_at, created_by, modified_at, modified_by
                 ) VALUES (
                     ?, ?, ?, ?,
                     ?, ?, ?,
                     ?, cast(? as inet), ?,
                     ?::jsonb, ?::jsonb, ?,
                     ?, ?,
                     ?, ?, ?, ?
                 )
                 """;

        jdbcClient.sql(sql).params(auditLog.getId().getRawValue(), // 1
                auditLog.getActorId(), // 2
                auditLog.getTargetId(), // 3
                auditLog.getTargetType(), // 4
                auditLog.getActionCategory(), // 5
                auditLog.getActionType(), // 6
                auditLog.getResultStatus(), // 7
                auditLog.getOccurredAt().atOffset(ZoneOffset.UTC), // 8 
                auditLog.getClientIp(), // 9
                auditLog.getUserAgent(), // 10
                auditLog.getBeforeData(), // 11
                auditLog.getAfterData(), // 12
                auditLog.getReason(), // 13
                auditLog.getIntegrityHash(), // 14
                auditLog.getMaskedFields(), // 15
                auditLog.getCreatedAt().atOffset(ZoneOffset.UTC), // 16
                auditLog.getCreatedBy(), // 17
                auditLog.getModifiedAt().atOffset(ZoneOffset.UTC), // 18
                auditLog.getModifiedBy() // 19
        ).update();
    }

    @Override
    public Optional<AuditLog> selectById(String id) {
        return jdbcClient.sql("SELECT * FROM system_audit_logs WHERE log_id = ?").param(id)
                .query(mapToAuditLog).optional();
    }

    @Override
    public List<AuditLog> findByTargetId(String targetId) {
        String sql = "SELECT * FROM system_audit_logs WHERE after_data @> :jsonQuery::jsonb";
        var jsonQuery = String.format("{\"targetId\": \"%s\"}", targetId);

        return jdbcClient.sql(sql).param("jsonQuery", jsonQuery).query(mapToAuditLog).list();
    }

    private final RowMapper<AuditLog> mapToAuditLog = (rs, rowNum) -> AuditLog.reconstitute(
            AuditLogId.Creator.fromPublic(rs.getString("log_id")), toInstant(rs, "created_at"),
            rs.getString("created_by"), toInstant(rs, "modified_at"), rs.getString("modified_by"),
            toInstant(rs, "occurred_at"), rs.getString("client_ip"), rs.getString("user_agent"),
            rs.getString("actor_id"), rs.getString("target_id"), rs.getString("target_type"),
            rs.getString("action_category"), rs.getString("action_type"),
            rs.getString("result_status"), rs.getString("before_data"), rs.getString("after_data"),
            rs.getString("reason"), rs.getString("integrity_hash"), rs.getString("masked_fields"));

    private Instant toInstant(ResultSet rs, String columnLabel) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(columnLabel);
        return timestamp != null ? timestamp.toInstant() : null;
    }

    @Override
    public List<AuditLog> selectByActorAndPeriod(String actorId, Instant start, Instant end) {
        String sql = """
                SELECT * FROM system_audit_logs
                WHERE actor_id = :actorId
                  AND occurred_at BETWEEN :start AND :end
                ORDER BY occurred_at DESC
                """;

        return jdbcClient.sql(sql).param("actorId", actorId)
                .param("start", start.atOffset(ZoneOffset.UTC))
                .param("end", end.atOffset(ZoneOffset.UTC)).query(mapToAuditLog).list();
    }

    @Override
    public List<AuditLog> selectByJsonContent(String key, String value) {
        // PostgreSQL JSONB 포함 연산자 (@>) 활용
        // 예: after_data @> '{"role": "ADMIN"}'
        String jsonFilter = String.format("{\"%s\": \"%s\"}", key, value);

        String sql = """
                SELECT * FROM system_audit_logs
                WHERE after_data @> :jsonFilter::jsonb
                ORDER BY occurred_at DESC
                """;

        return jdbcClient.sql(sql).param("jsonFilter", jsonFilter).query(mapToAuditLog).list();
    }

    @Override
    public List<AuditLogId> findIdsByData(LocalDate endedDate) {

        var startOfDay = endedDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        var nextDayStart = endedDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);

        String sql = """
                SELECT log_id
                FROM system_audit_logs
                WHERE occurred_at >= :start AND occurred_at < :nextDay
                """;

        return jdbcClient.sql(sql).param("start", startOfDay).param("nextDay", nextDayStart)
                .query((rs, rowNum) -> AuditLogId.Creator.fromPublic(rs.getString("log_id"))).list();
    }

    @Override
    public PagedData<AuditLog> findByTarget(String targetType, String targetId,
            AuditLogSearchRequest searchRequest, PageRequest pageRequest) {

        StringBuilder sql = new StringBuilder("SELECT * FROM system_audit_logs WHERE 1=1 ");
        Map<String, Object> params = new HashMap<>();

        sql.append(" AND target_type = :targetType ");
        params.put("targetType", targetType);

        if (targetId != null) {
            sql.append(" AND target_id = :targetId ");
            params.put("targetId", targetId);
        }

        appendSearchConditions(sql, params, searchRequest);

        String countSql = "SELECT COUNT(*) " + sql.substring(sql.indexOf("FROM"));
        long total = jdbcClient.sql(countSql).params(params).query(Long.class).single();

        sql.append(" ORDER BY occurred_at DESC ");
        sql.append(" LIMIT :limit OFFSET :offset ");
        params.put("limit", pageRequest.pageSize());
        params.put("offset", pageRequest.offset());

        List<AuditLog> logs =
                jdbcClient.sql(sql.toString()).params(params).query(mapToAuditLog).list();

        return new PagedData<>(logs,
                new Paged(pageRequest.pageNo(), pageRequest.pageSize(), total));
    }

    private void appendSearchConditions(StringBuilder sql, Map<String, Object> params,
            AuditLogSearchRequest req) {
        if (req == null)
            return;

        if (req.startDate() != null) {
            sql.append(" AND occurred_at >= :start ");
            params.put("start", req.startDate().atStartOfDay().atOffset(ZoneOffset.UTC));
        }
        if (req.endDate() != null) {
            sql.append(" AND occurred_at < :end ");
            params.put("end", req.endDate().plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC));
        }
        if (req.actionCategory() != null) {
            sql.append(" AND action_category = :category ");
            params.put("category", req.actionCategory());
        }
        if (req.resultStatus() != null) {
            sql.append(" AND result_status = :status ");
            params.put("status", req.resultStatus());
        }
    }

    @Override
    public PagedData<AuditLog> findByActor(String actorId, AuditLogSearchRequest searchRequest,
            PageRequest pageRequest) {

        StringBuilder sql =
                new StringBuilder("SELECT * FROM system_audit_logs WHERE actor_id = :actorId ");
        Map<String, Object> params = new HashMap<>();
        params.put("actorId", actorId);

        appendSearchConditions(sql, params, searchRequest);

        String countSql = "SELECT COUNT(*) " + sql.substring(sql.indexOf("FROM"));
        long total = jdbcClient.sql(countSql).params(params).query(Long.class).single();

        sql.append(" ORDER BY occurred_at DESC LIMIT :limit OFFSET :offset ");
        params.put("limit", pageRequest.pageSize());
        params.put("offset", pageRequest.offset());

        List<AuditLog> logs =
                jdbcClient.sql(sql.toString()).params(params).query(mapToAuditLog).list();

        return new PagedData<>(logs,
                new Paged(pageRequest.pageNo(), pageRequest.pageSize(), total));
    }


}
