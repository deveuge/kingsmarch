package com.deveuge.kingsmarch.engine.pieces;

import com.deveuge.kingsmarch.engine.Board;
import com.deveuge.kingsmarch.engine.Square;
import com.deveuge.kingsmarch.engine.types.Colour;

import lombok.Getter;
import lombok.Setter;

public class Pawn extends Piece {

	public final static String ALGEBRAIC_NOTATION = "P";
	
	@Getter
	@Setter
	public boolean capturableEnPassant;

	public Pawn() {
		super(ALGEBRAIC_NOTATION);
	}
	
	public Pawn(Colour colour) {
		super(ALGEBRAIC_NOTATION, colour);
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

		if (isFirstMove()) {
			return isForwardMovement || isForwarMovementTwoSquares;
		}

		return end.isOccupied() 
				? isCaptureMovement
				: isForwardMovement || isCaptureMovement && isEnPassantCapture(board, start, end);
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
