package dev.ohhoonim.business.board.model;

import java.util.UUID;
import dev.ohhoonim.component.model.unit.EntityId;

public  record UserId(UUID userId) implements EntityId<UUID> {

    public UserId {
        if (userId == null) {
            throw new BoardException("id 는 필수입니다.");
        }
    }

    public static Creator<UUID, UserId> Creator = new Creator<>() {

        @Override
        public UserId from(UUID internalId, UUID externalId) {
            throw new BoardException("지원하지 않는 기능입니다");
        }

        @Override
        public UserId fromPublic(String publicId) {
            return new UserId(UUID.fromString(publicId));
        }

        @Override
        public UserId generate() {
            return new UserId(UUID.randomUUID());
        }
    };

    @Override
    public UUID getRawValue() {
        return userId;
    }

    @Override
    public String getPublicValue() {
        return userId.toString();
    }

}

   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   