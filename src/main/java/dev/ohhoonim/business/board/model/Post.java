package dev.ohhoonim.business.board.model;

import java.time.Instant;
import dev.ohhoonim.business.board.model.PostComponent.User;
import dev.ohhoonim.component.model.unit.Entity;

public record Post(PostId postId, String title, String contents, Instant lastModifiedAt,
        User user) implements Entity<PostId> {
    public Post {
        if (title.isBlank() || contents.isBlank()) {
            throw new BoardException("제목은 필수 입니다.");
        }
    }

    @Override
    public PostId getId() {
        return postId;
    }
}
