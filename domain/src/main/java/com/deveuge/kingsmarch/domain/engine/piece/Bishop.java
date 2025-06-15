package com.deveuge.kingsmarch.domain.engine.piece;

import java.util.ArrayList;
import java.util.List;

import com.deveuge.kingsmarch.domain.engine.Board;
import com.deveuge.kingsmarch.domain.engine.Square;
import com.deveuge.kingsmarch.domain.model.MovementDirection;

public class Bishop extends Piece {

	public final static String ALGEBRAIC_NOTATION = "B";
	public final static int VALUE = 330;
	public final static int[][] POSITIONAL_VALUE = new int[][] {
	    {-220,-110,-110,-110,-110,-110,-110,-220},
	    {-110,  0,  0,  0,  0,  0,  0,-110},
	    {-110,  0,  55, 110, 110,  55,  0,-110},
	    {-110,  55,  55, 110, 110,  55,  55,-110},
	    {-110,  0, 110, 110, 110, 110,  0,-110},
	    {-110, 110, 110, 110, 110, 110, 110,-110},
	    {-110,  55,  0,  0,  0,  0,  55,-110},
	    {-220,-110,-110,-110,-110,-110,-110,-220}
	};

	public Bishop() {
		super(ALGEBRAIC_NOTATION, VALUE, POSITIONAL_VALUE);
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
