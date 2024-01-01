package com.deveuge.kingsmarch.engine.pieces;

import com.deveuge.kingsmarch.engine.Board;
import com.deveuge.kingsmarch.engine.Square;
import com.deveuge.kingsmarch.engine.types.MovementDirection;

public class Queen extends Piece {

	private final static String ALGEBRAIC_NOTATION = "Q";

	public Queen() {
		super(ALGEBRAIC_NOTATION);
	}
	
	/**
	 * <strong>Queen ♛</strong>: It can move any number of squares vertically,
	 * horizontally or diagonally, combining the powers of the rook and bishop.<br>
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isLegalMove(Board board, Square start, Square end) {
		int startRow = start.getRow();
		int endRow = end.getRow();
		int startCol = start.getCol();
		int endCol = end.getCol();

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
		case UP_RIGHT:
		case DOWN_LEFT:
		case DOWN_RIGHT:
			return checkDiagonalMovement(board, start, end);
		default:
			return false;
		}
	}
}
