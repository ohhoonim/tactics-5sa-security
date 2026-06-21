package dev.ohhoonim.system.auditlog.model;

import java.util.UUID;
import com.github.f4b6a3.ulid.UlidCreator;
import dev.ohhoonim.component.model.unit.EntityId;

public record AuditLogId(UUID value) implements EntityId <UUID>{

    public AuditLogId {
        if (value == null) {
            throw new AuditLogException("auditId가 존재하지 않습니다.");
        }
    }

    public static Creator<UUID, AuditLogId> Creator = new Creator<>() {

        @Override
        public AuditLogId from(UUID internalId, UUID externalId) {
            throw new AuditLogException("해당기능을 지원하지 않습니다.");
        }

        @Override
        public AuditLogId fromPublic(String value) {
            return new AuditLogId(UUID.fromString(value));
        }

        @Override
        public AuditLogId generate() {
            UUID ulidFormat = UlidCreator.getMonotonicUlid().toUuid(); 
            return new AuditLogId(ulidFormat);
        }
    };

    @Override
    public UUID getRawValue() {
        return this.value;
    }

    @Override
    public String getPublicValue() {
        return this.value.toString(); 
    }

}
