package com.deveuge.kingsmarch.engine.pieces;

import com.deveuge.kingsmarch.engine.Board;
import com.deveuge.kingsmarch.engine.Square;
import com.deveuge.kingsmarch.engine.types.MovementDirection;

public class Rook extends Piece {

	/**
	 * <strong>Rook ♜</strong>: It may move any number of squares horizontally or
	 * vertically without jumping.<br>
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isLegalMove(Board board, Square start, Square end) {
		boolean isWhite = start.getPiece().isWhite();
		int startRow = isWhite ? start.getRow() : end.getRow();
		int endRow = isWhite ? end.getRow() : start.getRow();
		int startCol = isWhite ? start.getCol() : end.getCol();
		int endCol = isWhite ? end.getCol(): start.getCol();

		switch (MovementDirection.get(start, end)) {
		case UP:
			return checkVerticalMovement(board, startRow + 1, endRow, startRow);
		case DOWN:
			return checkVerticalMovement(board, endRow + 1, startRow, startRow);
		case LEFT:
			return checkHorizontalMovement(board, endCol + 1, startCol, startCol);
		case RIGHT:
			return checkHorizontalMovement(board, startCol + 1, endCol, startCol);
		default:
			return false;
		}
	}
}
