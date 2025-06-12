package com.deveuge.kingsmarch.domain.engine;

import com.deveuge.kingsmarch.domain.engine.piece.Piece;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Square {

    private int row;
    private int col;
	private Piece piece;

	public boolean isOccupied() {
		return this.getPiece() != null;
	}
}
