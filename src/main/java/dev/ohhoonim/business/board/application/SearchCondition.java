package dev.ohhoonim.business.board.application;

import dev.ohhoonim.component.model.payload.Dto;

@Dto
public record SearchCondition(String searchWord) {

    public static SearchCondition all() {
        return new SearchCondition("");
    }
}
