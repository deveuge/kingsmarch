package com.deveuge.kingsmarch.engine.pieces;

import com.deveuge.kingsmarch.engine.Board;
import com.deveuge.kingsmarch.engine.Square;

public class Bishop extends Piece {

	private final static String ALGEBRAIC_NOTATION = "B";

	public Bishop() {
		super(ALGEBRAIC_NOTATION);
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
}
