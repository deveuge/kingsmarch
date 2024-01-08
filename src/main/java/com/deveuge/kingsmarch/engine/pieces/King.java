package com.deveuge.kingsmarch.engine.pieces;

import java.util.ArrayList;
import java.util.List;

import com.deveuge.kingsmarch.engine.Board;
import com.deveuge.kingsmarch.engine.Square;
import com.deveuge.kingsmarch.engine.types.MovementDirection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class King extends Piece {

	public final static String ALGEBRAIC_NOTATION = "K";
	public final static int VALUE = 900;
	public final static int[][] POSITIONAL_VALUE = new int[][] {
		{-30,-40,-40,-50,-50,-40,-40,-30},
		{-30,-40,-40,-50,-50,-40,-40,-30},
		{-30,-40,-40,-50,-50,-40,-40,-30},
		{-30,-40,-40,-50,-50,-40,-40,-30},
		{-20,-30,-30,-40,-40,-30,-30,-20},
		{-10,-20,-20,-20,-20,-20,-20,-10},
		{20, 20,  0,  0,  0,  0, 20, 20},
		{20, 30, 10,  0,  0, 10, 30, 20}
	};


	public King() {
		super(ALGEBRAIC_NOTATION, VALUE, POSITIONAL_VALUE);
	}
	
	/**
	 * <strong>King ♚</strong>: It may move to any adjoining square. It may also
	 * perform, in tandem with the rook, a special move called castling.<br>
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isLegalMove(Board board, Square start, Square end) {
		int verticalMovement = Math.abs(start.getRow() - end.getRow());
		int horizontalMovement = Math.abs(start.getCol() - end.getCol());
		
		if (isCastlingMove(start, end)) {
			return this.isValidCastling(board, start, end);
		}
		
		return verticalMovement < 2 && horizontalMovement < 2 && !isInCheck(board, end);
	}

	@Override
	public List<Square> getPotentialMoves(Board board, Square start) {
		List<Square> moves = new ArrayList<>();

		int startRow = start.getRow();
		int startCol = start.getCol();
		
		int[][] positions = {
				// Upper rank
				{startRow + 1, startCol},
				{startRow + 1, startCol - 1},
				{startRow + 1, startCol + 1},
				// Lower rank
				{startRow - 1, startCol},
				{startRow - 1, startCol - 1},
				{startRow - 1, startCol + 1},
				// Left square
				{startRow, startCol - 1},
				// Right square
				{startRow, startCol + 1}
		};
		
		for(int[] position : positions) {
			Square square = board.getSquare(position[0], position[1]);
			if(square != null) {
				moves.add(square);
			}
		}

		return moves;
	}

	/**
	 * Check whether the current start and end squares involve a castling move. This means:
	 * <ul>
	 * <li>The king is exchanged for the rook.</li>
	 * <li>Neither the king nor the rook has previously moved.</li>
	 * </ul>
	 * 
	 * @param start {@link Square} Starting position of the movement
	 * @param end   {@link Square} Final position of the movement
	 * @return true if the movement is a castling move, false otherwise
	 */
	public boolean isCastlingMove(Square start, Square end) {
		boolean isKingCorrect = start.getPiece().isFirstMove();
		Piece piece = end.getPiece();
		boolean isRookCorrect = piece != null && piece instanceof Rook && start.getPiece().isWhite() == piece.isWhite() && piece.isFirstMove();

		return isKingCorrect && isRookCorrect;
	}

	/**
	 * Checks whether the castling move direction is correct, regardless of whether
	 * it is kingside or queenside. This means:
	 * <ul>
	 * <li>There are no pieces between the king and the rook.</li>
	 * <li>The king does not pass through or finish on a square that is attacked by
	 * an enemy piece.</li>
	 * </ul>
	 * 
	 * @param board {@link Board} Current board situation
	 * @param start {@link Square} Starting position of the movement
	 * @param end   {@link Square} Final position of the movement
	 * @return true if the castling movement direction is allowed, false otherwise
	 */
	private boolean isValidCastling(Board board, Square start, Square end) {
		int startCol = start.getCol();
		int endCol = end.getCol();
		switch (MovementDirection.get(start, end)) {
		case LEFT:
			return checkHorizontalMovement(board, endCol + 1, startCol, start.getRow()) 
					&& !causesACheck(board, start.getRow(), end.getCol() + 1, start.getCol());
		case RIGHT:
			return checkHorizontalMovement(board, startCol + 1, endCol, start.getRow()) 
					&& !causesACheck(board, start.getRow(), start.getCol(), end.getCol() - 1);
		default:
			return false;
		}
	}

	/**
	 * Check if the king on this square is in check by the enemy pieces.
	 * 
	 * @param board  {@link Board} Current board situation
	 * @param square {@link Square} Position of the king piece
	 * @return true if the king is in check, false otherwise
	 */
	public boolean isInCheck(Board board, Square square) {
		List<Square> opponentSquares = board.getOccupiedSquares(this.getColour().getOpposite());
		for(Square opponentSquare : opponentSquares) {
			if(opponentSquare.getPiece().canMove(board, opponentSquare, square)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check if the king on this square is checkmated by the enemy pieces. Same
	 * logic as {@link King.isInCheck(Board board, Square square)} but without the
	 * check on whether the next move leaves the king in check.
	 * 
	 * @param board  {@link Board} Current board situation
	 * @param square {@link Square} Position of the king piece
	 * @return true if the king is checkmated, false otherwise
	 */
	public boolean isCheckmated(Board board, Square square) {
		List<Square> opponentSquares = board.getOccupiedSquares(this.getColour().getOpposite());
		for(Square opponentSquare : opponentSquares) {
			if(opponentSquare.getPiece().canMove(board, opponentSquare, square, false)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check if the king will go through a check by the enemy pieces.
	 * 
	 * @param board  {@link Board} Current board situation
	 * @param square List<{@link Square}> List of positions the king will go through
	 * @return true if the king will be through a check, false otherwise
	 */
	private boolean isInCheck(Board board, List<Square> squares) {
		List<Square> opponentSquares = board.getOccupiedSquares(this.getColour().getOpposite());
		for(Square opponentSquare : opponentSquares) {
			for(Square destinationSquare : squares) {
				if(opponentSquare.getPiece().canMove(board, opponentSquare, destinationSquare)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check if the king passes through a check on the indicated move. The starting
	 * square is also taken into account for this validation.
	 * 
	 * @param board {@link Board} Current board situation
	 * @param row   Index of the row to verify
	 * @param start Index of the initial column
	 * @param end   Index of the final column
	 * @return true if the king goes through a check, false otherwise
	 */
	private boolean causesACheck(Board board, int row, int start, int end) {
		List<Square> squaresToCheck = new ArrayList<>();
		for (int i = start; i <= end; i++) {
			squaresToCheck.add(board.getSquare(row, i));
		}
		return isInCheck(board, squaresToCheck);
	}

}
