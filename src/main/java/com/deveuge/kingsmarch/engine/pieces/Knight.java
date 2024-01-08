package com.deveuge.kingsmarch.engine.pieces;

import java.util.ArrayList;
import java.util.List;

import com.deveuge.kingsmarch.engine.Board;
import com.deveuge.kingsmarch.engine.Square;

public class Knight extends Piece {

	public final static String ALGEBRAIC_NOTATION = "N";
	public final static int VALUE = 30;
	public final static int[][] POSITIONAL_VALUE = new int[][] {
		{-50,-40,-30,-30,-30,-30,-40,-50},
		{-40,-20,  0,  0,  0,  0,-20,-40},
		{-30,  0, 10, 15, 15, 10,  0,-30},
		{-30,  5, 15, 20, 20, 15,  5,-30},
		{-30,  0, 15, 20, 20, 15,  0,-30},
		{-30,  5, 10, 15, 15, 10,  5,-30},
		{-40,-20,  0,  5,  5,  0,-20,-40},
		{-50,-40,-30,-30,-30,-30,-40,-50}
	};

	public Knight() {
		super(ALGEBRAIC_NOTATION, VALUE, POSITIONAL_VALUE);
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

	@Override
	public List<Square> getPotentialMoves(Board board, Square start) {
		List<Square> moves = new ArrayList<>();
		
		int startCol = start.getCol();
		int startRow = start.getRow();
		
		int[][] positions = {
				// Up left
				{startRow + 1, startCol - 2},
				{startRow + 2, startCol - 1},
				// Up right
				{startRow + 1, startCol + 2},
				{startRow + 2, startCol + 1},
				// Down left
				{startRow - 1, startCol - 2},
				{startRow - 2, startCol - 1},
				// Down right
				{startRow - 1, startCol + 2},
				{startRow - 2, startCol + 1}
		};

		for(int[] position : positions) {
			Square square = board.getSquare(position[0], position[1]);
			if(square != null) {
				moves.add(square);
			}
		}

		return moves;
	}

}
