package dev.ohhoonim.business.board.application;

import java.time.Instant;
import dev.ohhoonim.business.board.model.PostId;
import dev.ohhoonim.component.model.payload.Dto;

@Dto
public record PostResponse(
    PostId postId,
    String title,
    String contents,
    Instant lastModifiedAt,
    String username
) {

}
