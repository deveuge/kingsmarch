package com.deveuge.kingsmarch.engine.pieces;

import java.util.ArrayList;
import java.util.List;

import com.deveuge.kingsmarch.engine.Board;
import com.deveuge.kingsmarch.engine.Square;
import com.deveuge.kingsmarch.engine.types.MovementDirection;

public class Bishop extends Piece {

	public final static String ALGEBRAIC_NOTATION = "B";
	public final static int VALUE = 30;

	public Bishop() {
		super(ALGEBRAIC_NOTATION, VALUE);
	}
	
	/**
	 * <strong>Bishop ♝</strong>: It moves and captures along diagonals without
	 * jumping over intervening pieces.<br>
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isLegalMove(Board board, Square start, Square end) {
		return checkDiagonalMovement(board, start, end);
	}

	@Override
	public List<Square> getPotentialMoves(Board board, Square start) {
		List<Square> moves = new ArrayList<>();
		moves.addAll(getPotentialDiagonalMoves(board, start, MovementDirection.UP_LEFT));
		moves.addAll(getPotentialDiagonalMoves(board, start, MovementDirection.UP_RIGHT));
		moves.addAll(getPotentialDiagonalMoves(board, start, MovementDirection.DOWN_LEFT));
		moves.addAll(getPotentialDiagonalMoves(board, start, MovementDirection.DOWN_RIGHT));
		return moves;
	}
}
