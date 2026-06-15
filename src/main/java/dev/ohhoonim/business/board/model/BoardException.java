package dev.ohhoonim.business.board.model;

import dev.ohhoonim.component.model.unit.DomainException;

@DomainException
public class BoardException extends RuntimeException {

    public BoardException(String message) {
        super(message);
    }
}
