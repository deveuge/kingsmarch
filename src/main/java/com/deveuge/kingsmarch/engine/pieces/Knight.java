package com.deveuge.kingsmarch.engine.pieces;

import com.deveuge.kingsmarch.engine.Board;
import com.deveuge.kingsmarch.engine.Square;

public class Knight extends Piece {

	private final static String ALGEBRAIC_NOTATION = "N";

	public Knight() {
		super(ALGEBRAIC_NOTATION);
	}
	
	/**
	 * <strong>Knight ♞</strong>: It moves two squares vertically and one square
	 * horizontally, or two squares horizontally and one square vertically, jumping
	 * over other pieces.<br>
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isLegalMove(Board board, Square start, Square end) {
		int verticalMovement = Math.abs(start.getRow() - end.getRow());
		int horizontalMovement = Math.abs(start.getCol() - end.getCol());
		return verticalMovement * horizontalMovement == 2;
	}

}
