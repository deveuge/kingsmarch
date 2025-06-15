package com.deveuge.kingsmarch.domain.engine.piece;

import java.util.ArrayList;
import java.util.List;

import com.deveuge.kingsmarch.domain.engine.Board;
import com.deveuge.kingsmarch.domain.engine.Square;

public class Knight extends Piece {

	public final static String ALGEBRAIC_NOTATION = "N";
	public final static int VALUE = 320;
	public final static int[][] POSITIONAL_VALUE = new int[][] {
	    {-533,-427,-320,-320,-320,-320,-427,-533},
	    {-427,-213,  0,  0,  0,  0,-213,-427},
	    {-320,  0, 107, 160, 160, 107,  0,-320},
	    {-320,  53, 160, 213, 213, 160,  53,-320},
	    {-320,  0, 160, 213, 213, 160,  0,-320},
	    {-320,  53, 107, 160, 160, 107,  53,-320},
	    {-427,-213,  0,  53,  53,  0,-213,-427},
	    {-533,-427,-320,-320,-320,-320,-427,-533}
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
