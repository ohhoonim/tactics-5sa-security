package dev.ohhoonim.business.board.model;

import dev.ohhoonim.business.board.model.PostComponent.User;
import dev.ohhoonim.component.model.unit.ValueObject;

@ValueObject
public sealed interface PostComponent permits User {

    public record User(UserId lastModifiedBy, String nickName) implements PostComponent {
        public User {
            if (nickName == null) {
                throw new BoardException("nick name은 필수입니다.");
            }
            if (lastModifiedBy == null) {
                lastModifiedBy = UserId.Creator.generate();
            }
        }
    }
}
