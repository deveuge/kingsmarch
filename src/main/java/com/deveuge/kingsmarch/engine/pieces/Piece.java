package com.deveuge.kingsmarch.engine.pieces;

import com.deveuge.kingsmarch.engine.Board;
import com.deveuge.kingsmarch.engine.Square;
import com.deveuge.kingsmarch.engine.types.Colour;
import com.deveuge.kingsmarch.engine.types.MovementDirection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Piece {

	private String algebraicNotation; 
	private Colour colour = Colour.WHITE;
	private boolean firstMove = true;
	
	public Piece(String algebraicNotation) {
		super();
		this.algebraicNotation = algebraicNotation;
	}
	
	public Piece(String algebraicNotation, Colour colour) {
		super();
		this.colour = colour;
		this.algebraicNotation = algebraicNotation;
	}
	
	/**
	 * Creates a new piece to replace the pawn after promotion.
	 * 
	 * @param pieceNotation {@link String} Algebraic notation of the new piece
	 * @param colour        {@link Colour} Colour of the piece to be created
	 * @return {@link Piece}
	 */
	public static final Piece createPromotionPiece(String pieceNotation, Colour colour) {
		try {
			Piece newPiece = getPromotionClass(pieceNotation).getDeclaredConstructor().newInstance();
	    	newPiece.setColour(colour);
	        return newPiece;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets the class corresponding to the algebraic notation passed by parameter.
	 * Only those classes of pieces to which a pawn can be promoted are taken into
	 * account.
	 * 
	 * @param pieceNotation {@link String} Algebraic notation of the piece
	 * @return {@link Class}{@literal <}? extends {@link Piece}>
	 */
    private static Class<? extends Piece> getPromotionClass(String pieceNotation) {
    	switch(pieceNotation.toUpperCase()) {
    	case Queen.ALGEBRAIC_NOTATION:
    		return Queen.class;
    	case Rook.ALGEBRAIC_NOTATION:
    		return Rook.class;
    	case Bishop.ALGEBRAIC_NOTATION:
    		return Bishop.class;
    	case Knight.ALGEBRAIC_NOTATION:
    		return Knight.class;
    	}
    	return null;
    }
	
	/**
	 * Checks if the piece is white.
	 * @return true if the piece is white, false otherwise
	 */
	public boolean isWhite() {
		return colour.isWhite();
	}

	/**
	 * Checks if the piece's movement is allowed with the current board situation.
	 * Each inheritance of the Piece class must implement this method to establish
	 * the individual logic of each piece.
	 * 
	 * @param board {@link Board} Current board situation
	 * @param start {@link Square} Starting position of the movement
	 * @param end   {@link Square} Final position of the movement
	 * @return true if the movement is allowed, false otherwise
	 */
	protected abstract boolean isLegalMove(Board board, Square start, Square end);

	/**
	 * Checks if the piece's movement is allowed with the current board situation.
	 * General method incorporating part-specific logic and generic checks.
	 * 
	 * @param board {@link Board} Current board situation
	 * @param start {@link Square} Starting position of the movement
	 * @param end   {@link Square} Final position of the movement
	 * @return true if the movement is allowed, false otherwise
	 */
	public final boolean canMove(Board board, Square start, Square end) {
		if (isDestinationSameAsCurrent(start, end) 
				|| (isSquareOccupiedBySameColourPiece(this, end) && !isCastlingMoveException(this, end))) {
			return false;
		}

		return isLegalMove(board, start, end) && !leavesKingInCheck(board, start, end);
	}

	/**
	 * Check if the move involves moving to the same starting square.
	 * 
	 * @param start {@link Square} Starting position of the movement
	 * @param end   {@link Square} Final position of the movement
	 * @return true if the final position is the same as the initial one, false
	 *         otherwise
	 */
	private boolean isDestinationSameAsCurrent(Square start, Square end) {
		return start.getRow() == end.getRow() && start.getCol() == end.getCol();
	}

	/**
	 * Checks if there is already a piece of the same colour in the square.
	 * 
	 * @param piece {@link Piece} User piece to be used as colour checker
	 * @param end   {@link Square} Final position of the movement
	 * @return true if the square is occupied by a piece of the same colour, false
	 *         otherwise
	 */
	private final boolean isSquareOccupiedBySameColourPiece(Piece piece, Square end) {
		return end.isOccupied() && piece.isWhite() == end.getPiece().isWhite();
	}

	/**
	 * Checks if the current move is a castling movement. That is, the king is
	 * trying to exchange positions with the rook. This method does not validate
	 * possible checks and other pieces positions.
	 * 
	 * @param piece {@link Piece} User piece to be used as colour checker
	 * @param end   {@link Square} Final position of the movement
	 * @return true if is a castling movement, false otherwise
	 */
	private final boolean isCastlingMoveException(Piece piece, Square end) {
		boolean isKing = piece instanceof King && piece.isFirstMove();
		boolean isRook = end.getPiece() != null && end.getPiece() instanceof Rook && end.getPiece().isFirstMove();
		return isKing && isRook;
	}
	
	/**
	 * Verifies that, if the move is performed, the king is not left in check.
	 * 
	 * @param board {@link Board} Current board situation
	 * @param start {@link Square} Starting position of the movement
	 * @param end   {@link Square} Final position of the movement
	 * @return true if the king will be in check, false otherwise
	 */
	private boolean leavesKingInCheck(Board board, Square start, Square end) {
		Square kingSquare = board.getKingSquare(this.getColour());
		if(this instanceof King || kingSquare == null) {
			return false;
		}
		King king = (King) kingSquare.getPiece();
		Board temporalBoard = new Board(board);
		temporalBoard.getSquare(start.getRow(), start.getCol()).setPiece(null);
		temporalBoard.getSquare(end.getRow(), end.getCol()).setPiece(this);
		return king.isInCheck(temporalBoard, kingSquare);
	}

	/**
	 * Checks if there are other pieces in between the start and end of the
	 * horizontal movement.
	 * 
	 * @param board {@link Board} Current board situation
	 * @param start Index of the initial column
	 * @param end   Index of the final column
	 * @param row   Index of the row to verify
	 * @return true if there are other pieces in between, false otherwise
	 */
	public final boolean checkHorizontalMovement(Board board, int start, int end, int row) {
		for (int i = start; i < end; i++) {
			if (board.getSquare(row, i).isOccupied()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if there are other pieces in between the start and end of the vertical
	 * movement.
	 * 
	 * @param board {@link Board} Current board situation
	 * @param start Index of the initial row
	 * @param end   Index of the final row
	 * @param col   Index of the column to verify
	 * @return true if there are other pieces in between, false otherwise
	 */
	public final boolean checkVerticalMovement(Board board, int start, int end, int col) {
		for (int i = start; i < end; i++) {
			if (board.getSquare(i, col).isOccupied()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if there are other pieces in between the start and end of the diagonal
	 * movement.
	 * 
	 * @param board {@link Board} Current board situation
	 * @param start {@link Square} Starting position of the movement
	 * @param end   {@link Square} Final position of the movement
	 * @return true if there are other pieces in between, false otherwise
	 */
	public final boolean checkDiagonalMovement(Board board, Square start, Square end) {
		int startRow = start.getRow();
		int endRow = end.getRow();
		int startCol = start.getCol();
		int endCol = end.getCol();
		
		switch (MovementDirection.get(start, end)) {
		case UP_LEFT:
			for(int i = (startRow + 1), j = (startCol - 1); i < endRow && j > endCol; i++, j--) {
				if (board.getSquare(i, j).isOccupied()) {
					return false;
				}
			}
			return true;
		case UP_RIGHT:
			for(int i = (startRow + 1), j = (startCol + 1); i < endRow && j < endCol; i++, j++) {
				if (board.getSquare(i, j).isOccupied()) {
					return false;
				}
			}
			return true;
		case DOWN_LEFT:
			for(int i = (startRow - 1), j = (startCol - 1); i > endRow && j > endCol; i--, j--) {
				if (board.getSquare(i, j).isOccupied()) {
					return false;
				}
			}
			return true;
		case DOWN_RIGHT:
			for(int i = (startRow - 1), j = (startCol + 1); i > endRow && j < endCol; i--, j++) {
				if (board.getSquare(i, j).isOccupied()) {
					return false;
				}
			}
			return true;
		default:
			return false;
		}
	}

}
