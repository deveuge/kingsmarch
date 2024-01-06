package com.deveuge.kingsmarch.engine.pieces;

import java.util.ArrayList;
import java.util.List;

import com.deveuge.kingsmarch.engine.Board;
import com.deveuge.kingsmarch.engine.Square;
import com.deveuge.kingsmarch.engine.types.MovementDirection;

public class Rook extends Piece {

	public final static String ALGEBRAIC_NOTATION = "R";

	public Rook() {
		super(ALGEBRAIC_NOTATION);
	}
	
	/**
	 * <strong>Rook ♜</strong>: It may move any number of squares horizontally or
	 * vertically without jumping.<br>
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
		default:
			return false;
		}
	}

	@Override
	public List<Square> getPotentialMoves(Board board, Square start) {
		List<Square> moves = new ArrayList<>();
		moves.addAll(getPotentialVerticalMoves(board, start));
		moves.addAll(getPotentialHorizontalMoves(board, start));
		return moves;
	}
}
