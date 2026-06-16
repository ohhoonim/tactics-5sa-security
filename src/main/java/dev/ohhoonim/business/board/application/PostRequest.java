package dev.ohhoonim.business.board.application;

import dev.ohhoonim.component.model.payload.Dto;

@Dto
public record PostRequest(String title, String contents, String nickname) {

}
