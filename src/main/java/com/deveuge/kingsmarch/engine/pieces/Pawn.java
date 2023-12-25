package com.deveuge.kingsmarch.engine.pieces;

import com.deveuge.kingsmarch.engine.Board;
import com.deveuge.kingsmarch.engine.Square;
import com.deveuge.kingsmarch.engine.types.Colour;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Pawn extends Piece {

	public Pawn(Colour colour) {
		super(colour);
	}

	/**
	 * <strong>Pawn ♟</strong>: It may move one square directly forward, it may move
	 * two squares directly forward on its first move, and it may capture one square
	 * diagonally forward.<br>
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isLegalMove(Board board, Square start, Square end) {
		int verticalMovement = start.getPiece().isWhite() ? end.getRow() - start.getRow() : start.getRow() - end.getRow();
		int horizontalMovement = Math.abs(end.getCol() - start.getCol());

		if (isFirstMove()) {
			return horizontalMovement == 0 && (verticalMovement == 1 || verticalMovement == 2);
		}

		return end.isOccupied() 
				? (verticalMovement == 1 && horizontalMovement == 1)
				: (verticalMovement == 1 && horizontalMovement == 0);

		// TODO En passant
	}
}
