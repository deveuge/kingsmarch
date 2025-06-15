package com.deveuge.kingsmarch.domain.engine.piece;

import java.util.ArrayList;
import java.util.List;

import com.deveuge.kingsmarch.domain.engine.Board;
import com.deveuge.kingsmarch.domain.engine.Square;
import com.deveuge.kingsmarch.domain.model.MovementDirection;

public class Queen extends Piece {

	public final static String ALGEBRAIC_NOTATION = "Q";
	public final static int VALUE = 900;
	public final static int[][] POSITIONAL_VALUE = new int[][] {
	    {-200,-100,-100, -50, -50,-100,-100,-200},
	    {-100,   0,   0,   0,   0,   0,   0,-100},
	    {-100,   0,  50,  50,  50,  50,   0,-100},
	    {-50,   0,  50,  50,  50,  50,   0, -50},
	    {0,   0,  50,  50,  50,  50,   0, -50},
	    {-100,  50,  50,  50,  50,  50,   0,-100},
	    {-100,   0,  50,   0,   0,   0,   0,-100},
	    {-200,-100,-100, -50, -50,-100,-100,-200}
	};

	public Queen() {
		super(ALGEBRAIC_NOTATION, VALUE, POSITIONAL_VALUE);
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
			return checkVerticalMovement(board, startRow + 1, endRow, startCol);
		case DOWN:
			return checkVerticalMovement(board, endRow + 1, startRow, startCol);
		case LEFT:
			return checkHorizontalMovement(board, endCol + 1, startCol, startRow);
		case RIGHT:
			return checkHorizontalMovement(board, startCol + 1, endCol, startRow);
		case UP_LEFT:
		case UP_RIGHT:
		case DOWN_LEFT:
		case DOWN_RIGHT:
			return checkDiagonalMovement(board, start, end);
		default:
			return false;
		}
	}

	@Override
	public List<Square> getPotentialMoves(Board board, Square start) {
		List<Square> moves = new ArrayList<>();
		moves.addAll(getPotentialVerticalMoves(board, start));
		moves.addAll(getPotentialHorizontalMoves(board, start));
		moves.addAll(getPotentialDiagonalMoves(board, start, MovementDirection.UP_LEFT));
		moves.addAll(getPotentialDiagonalMoves(board, start, MovementDirection.UP_RIGHT));
		moves.addAll(getPotentialDiagonalMoves(board, start, MovementDirection.DOWN_LEFT));
		moves.addAll(getPotentialDiagonalMoves(board, start, MovementDirection.DOWN_RIGHT));
		return moves;
	}
}
