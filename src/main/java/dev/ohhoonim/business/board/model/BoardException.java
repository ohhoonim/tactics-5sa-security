package dev.ohhoonim.business.board.model;

import dev.ohhoonim.component.model.unit.DomainException;

public class BoardException extends DomainException {

    public BoardException(String message) {
        super(message);
    }

    @Override
    public String errorCode() {
        return "Board 에러";
    }
}
