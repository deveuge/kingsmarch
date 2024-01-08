package com.deveuge.kingsmarch.engine.pieces;

import java.util.ArrayList;
import java.util.List;

import com.deveuge.kingsmarch.engine.Board;
import com.deveuge.kingsmarch.engine.Square;
import com.deveuge.kingsmarch.engine.types.Colour;

import lombok.Getter;
import lombok.Setter;

public class Pawn extends Piece {

	public final static String ALGEBRAIC_NOTATION = "P";
	public final static int VALUE = 10;
	public final static int[][] POSITIONAL_VALUE = new int[][] {
		{0,  0,  0,  0,  0,  0,  0,  0},
		{50, 50, 50, 50, 50, 50, 50, 50},
		{10, 10, 20, 30, 30, 20, 10, 10},
		{5,  5, 10, 25, 25, 10,  5,  5},
		{0,  0,  0, 20, 20,  0,  0,  0},
		{5, -5,-10,  0,  0,-10, -5,  5},
		{5, 10, 10,-20,-20, 10, 10,  5},
		{0,  0,  0,  0,  0,  0,  0,  0},
	};
	
	@Getter
	@Setter
	public boolean capturableEnPassant;

	public Pawn() {
		super(ALGEBRAIC_NOTATION, VALUE, POSITIONAL_VALUE);
	}
	
	public Pawn(Colour colour) {
		super(ALGEBRAIC_NOTATION, VALUE, POSITIONAL_VALUE, colour);
	}

	/**
	 * <strong>Pawn ♟</strong>: It may move one square directly forward, it may move
	 * two squares directly forward on its first move, and it may capture one square
	 * diagonally forward.<br>
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isLegalMove(Board board, Square start, Square end) {
		int verticalMovement = start.getPiece().isWhite() 
				? (end.getRow() - start.getRow()) 
				: (start.getRow() - end.getRow());
		int horizontalMovement = Math.abs(end.getCol() - start.getCol());
		
		boolean isCaptureMovement = verticalMovement == 1 && horizontalMovement == 1;
		boolean isForwardMovement = verticalMovement == 1 && horizontalMovement == 0;
		boolean isForwarMovementTwoSquares = verticalMovement == 2 && horizontalMovement == 0;

		boolean isForwardMovementAllowed = isFirstMove()
				? isForwardMovement || isForwarMovementTwoSquares
				: isForwardMovement;

		return end.isOccupied() 
				? isCaptureMovement
				: isForwardMovementAllowed || isCaptureMovement && isEnPassantCapture(board, start, end);
	}
	
	@Override
	public List<Square> getPotentialMoves(Board board, Square start) {
		List<Square> moves = new ArrayList<>();

		int startRow = start.getRow();
		int startCol = start.getCol();
		
		List<int[]> positions = new ArrayList<>();
		
		if(this.getColour().isWhite()) {
			// Upper rank + captures
			positions.add(new int[] {startRow + 1, startCol});
			positions.add(new int[] {startRow + 1, startCol + 1});
			positions.add(new int[] {startRow + 1, startCol - 1});
			if(this.isFirstMove()) {
				// First move
				positions.add(new int[] {startRow + 2, startCol});
			}
		} else {
			// Lower rank + captures
			positions.add(new int[] {startRow - 1, startCol});
			positions.add(new int[] {startRow - 1, startCol + 1});
			positions.add(new int[] {startRow - 1, startCol - 1});
			if(this.isFirstMove()) {
				// First move
				positions.add(new int[] {startRow - 2, startCol});
			}
		}
		
		for(int[] position : positions) {
			Square square = board.getSquare(position[0], position[1]);
			if(square != null) {
				moves.add(square);
			}
		}

		return moves;
	}

	/**
	 * Checks if the pawn is making a capture en passant.
	 * 
	 * @param board {@link Board} Current board situation
	 * @param start {@link Square} Starting position of the movement
	 * @param end   {@link Square} Final position of the movement
	 * @return true if is an en passant capture, false otherwise
	 */
	public boolean isEnPassantCapture(Board board, Square start, Square end) {
		Square pawnSquare = getEnPassantPawnSquare(board, start, end);
		return pawnSquare != null && pawnSquare.getPiece() instanceof Pawn && ((Pawn) pawnSquare.getPiece()).isCapturableEnPassant();
	}
	
	/**
	 * Gets the square on which the pawn to be captured en passant is located.
	 * 
	 * @param board {@link Board} Current board situation
	 * @param start {@link Square} Starting position of the movement
	 * @param end   {@link Square} Final position of the movement
	 * @return {@link Square}
	 */
	public Square getEnPassantPawnSquare(Board board, Square start, Square end) {
		int pawnRow = start.getPiece().isWhite() ? end.getRow() - 1 : end.getRow() + 1;
		if(pawnRow < 0 || pawnRow > 7 ) {
			return null;
		}
		return board.getSquare(pawnRow, end.getCol());
	}
}
