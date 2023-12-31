package com.deveuge.kingsmarch.engine.pieces;

import com.deveuge.kingsmarch.engine.Board;
import com.deveuge.kingsmarch.engine.Square;
import com.deveuge.kingsmarch.engine.types.MovementDirection;

public class Queen extends Piece {

	/**
	 * <strong>Queen ♛</strong>: It can move any number of squares vertically,
	 * horizontally or diagonally, combining the powers of the rook and bishop.<br>
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
