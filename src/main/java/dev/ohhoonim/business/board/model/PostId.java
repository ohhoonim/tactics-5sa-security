package dev.ohhoonim.business.board.model;

import java.util.UUID;
import dev.ohhoonim.component.model.unit.EntityId;

public record PostId(UUID postId) implements EntityId<UUID>{

    public static Creator<UUID, PostId> Creator = new Creator<>() {

        @Override
        public PostId from(UUID internalId, UUID externalId) {
            throw new BoardException("지원하지 않는 기능입니다.");
        }

        @Override
        public PostId fromPublic(String publicId) {
            return new PostId(UUID.fromString(publicId));
        }

        @Override
        public PostId generate() {
            return new PostId(UUID.randomUUID());
        }
        
    };

    @Override
    public UUID getRawValue() {
        return postId;
    }

    @Override
    public String getPublicValue() {
        return postId.toString();
    }

}
