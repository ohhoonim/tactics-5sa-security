package dev.ohhoonim.system.user.model;

import java.util.UUID;
import com.github.f4b6a3.ulid.UlidCreator;
import dev.ohhoonim.component.model.unit.EntityId;

public record UserId(UUID internalId, UUID externalId) implements EntityId<UUID> {

    public UserId {
        if (externalId == null) {
            throw new UserException("외부 식별자가 없습니다");
        }
    }

    public static Creator<UUID, UserId> Creator = new Creator<>() {

        @Override
        public UserId from(UUID internalId, UUID externalId) {
            if (internalId == null)
                throw new UserException("내부 식별자가 누락되었습니다");
            return new UserId(internalId, externalId);
        }

        @Override
        public UserId fromPublic(String publicId) {
            return new UserId(null, UUID.fromString(publicId));
        }

        @Override
        public UserId generate() {
            return new UserId(UlidCreator.getUlid().toUuid(), UUID.randomUUID());
        }
    };

    @Override
    public UUID getRawValue() {
        if (internalId == null) {
            throw new UserException("내부 식별자가 확인되지 않은 ID입니다. Resolve가 필요합니다.");
        }
        return internalId;
    }

    @Override
    public String getPublicValue() {
        return externalId.toString();
    }
}
