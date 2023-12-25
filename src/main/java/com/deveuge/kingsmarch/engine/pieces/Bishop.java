package com.deveuge.kingsmarch.engine.pieces;

import com.deveuge.kingsmarch.engine.Board;
import com.deveuge.kingsmarch.engine.Square;
import com.deveuge.kingsmarch.engine.types.MovementDirection;

public class Bishop extends Piece {

	/**
	 * <strong>Bishop ♝</strong>: It moves and captures along diagonals without
	 * jumping over intervening pieces.<br>
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isLegalMove(Board board, Square start, Square end) {
		int startRow = start.getRow();
		int endRow = end.getRow();
		int startCol = start.getCol();
		int endCol = end.getCol();

		switch (MovementDirection.get(start, end)) {
		case UP_LEFT:
			return checkDiagonalMovement(board, startRow + 1, endCol + 1, endRow, startCol);
		case UP_RIGHT:
			return checkDiagonalMovement(board, startRow + 1, startCol + 1, endRow, endCol);
		case DOWN_LEFT:
			return checkDiagonalMovement(board, endRow + 1, endCol + 1, startRow, startCol);
		case DOWN_RIGHT:
			return checkDiagonalMovement(board, endRow + 1, startCol + 1, startRow, endCol);
		default:
			return false;
		}
	}
}
