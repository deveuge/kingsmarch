package com.deveuge.kingsmarch.engine.pieces;

import java.util.ArrayList;
import java.util.List;

import com.deveuge.kingsmarch.GameHelper;
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
	private int value;
	private Colour colour = Colour.WHITE;
	private boolean firstMove = true;
	
	public Piece(String algebraicNotation, int value) {
		super();
		this.algebraicNotation = algebraicNotation;
		this.value = value;
	}
	
	public Piece(String algebraicNotation, int value, Colour colour) {
		super();
		this.algebraicNotation = algebraicNotation;
		this.value = value;
		this.colour = colour;
	}
	
	/**
	 * Creates a new piece.
	 * 
	 * @param pieceNotation {@link String} Algebraic notation of the new piece
	 * @return {@link Piece}
	 */
	public static final Piece createFromAlgebraicNotation(String pieceNotation) {
		try {
			Piece newPiece = getClass(pieceNotation).getDeclaredConstructor().newInstance();
			Colour colour = Character.isUpperCase(pieceNotation.charAt(0)) ? Colour.WHITE : Colour.BLACK;
	    	newPiece.setColour(colour);
	        return newPiece;
        } catch (Exception e) {
			return null;
		}
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
	 * 
	 * @param pieceNotation {@link String} Algebraic notation of the piece
	 * @return {@link Class}{@literal <}? extends {@link Piece}>
	 */
    private static Class<? extends Piece> getClass(String pieceNotation) {
    	switch(pieceNotation.toUpperCase()) {
    	case Queen.ALGEBRAIC_NOTATION:
    		return Queen.class;
    	case Rook.ALGEBRAIC_NOTATION:
    		return Rook.class;
    	case Bishop.ALGEBRAIC_NOTATION:
    		return Bishop.class;
    	case Knight.ALGEBRAIC_NOTATION:
    		return Knight.class;
    	case King.ALGEBRAIC_NOTATION:
    		return King.class;
    	case Pawn.ALGEBRAIC_NOTATION:
    		return Pawn.class;
    	}
    	return null;
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
	 * Evaluates the current position of the piece on the board and returns an array
	 * of all the squares that it might be possible for the piece to move to.
	 * <strong>This method does not evaluate whether movements to these squares are legal.</strong>
	 * 
	 * @param board {@link Board} Current board situation
	 * @param start {@link Square} Starting position of the movement
	 * @return {@link List}<{@link Square}> List of squares the piece can potentially be moved to
	 */
	public abstract List<Square> getPotentialMoves(Board board, Square start);

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
	 * @param board           {@link Board} Current board situation
	 * @param start           {@link Square} Starting position of the movement
	 * @param end             {@link Square} Final position of the movement
	 * @param verifyKingCheck boolean Whether to verify if the movement leaves the
	 *                        king in check (false to prevent double checkmate)
	 * @return true if the movement is allowed, false otherwise
	 */
	public final boolean canMove(Board board, Square start, Square end, boolean verifyKingCheck) {
		if (isDestinationSameAsCurrent(start, end) 
				|| (isSquareOccupiedBySameColourPiece(this, end) && !isCastlingMoveException(this, end))) {
			return false;
		}
		
		boolean leavesKingInCheck = verifyKingCheck ? leavesKingInCheck(board, start, end) : false;
		return isLegalMove(board, start, end) && !leavesKingInCheck;
	}
	
	/**
	 * Checks if the piece's movement is allowed with the current board situation.
	 * <strong>Calls to: {@link Piece.canMove(Board board, Square start, Square end,
	 * boolean verifyKingCheck)} with verifyKingCheck value as "true"</strong>
	 * 
	 * @param board {@link Board} Current board situation
	 * @param start {@link Square} Starting position of the movement
	 * @param end   {@link Square} Final position of the movement
	 * @return true if the movement is allowed, false otherwise
	 */
	public final boolean canMove(Board board, Square start, Square end) {
		return canMove(board, start, end, true);
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
	 * Verifies that, if the move is performed, the king is not left in check or checkmate.
	 * 
	 * @param board {@link Board} Current board situation
	 * @param start {@link Square} Starting position of the movement
	 * @param end   {@link Square} Final position of the movement
	 * @return true if the king will be in check, false otherwise
	 */
	private boolean leavesKingInCheck(Board board, Square start, Square end) {

		Board temporalBoard = GameHelper.makeTemporalMove(board, start, end, this);
		Square kingSquare = temporalBoard.getKingSquare(this.getColour());
		if(kingSquare == null) {
			return true;
		}
		King king = (King) kingSquare.getPiece();
		return king.isInCheck(temporalBoard, kingSquare) || king.isCheckmated(temporalBoard, kingSquare);
	}

	/**
	 * Checks if there are other pieces in between the start and end of the
	 * horizontal movement.
	 * 
	 * @param board {@link Board} Current board situation
	 * @param start Index of the initial column
	 * @param end   Index of the final column
	 * @param row   Index of the row to verify
	 * @return true if there are no other pieces in between, false otherwise
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
	 * @return true if there are no other pieces in between, false otherwise
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
	 * @return true if there are no other pieces in between, false otherwise
	 */
	final boolean checkDiagonalMovement(Board board, Square start, Square end) {
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
	
	/**
	 * Obtains all the squares that it might be possible for the piece to move to horizontally.
	 * <strong>This method does not evaluate whether movements to these squares are legal.</strong>
	 * 
	 * @param board {@link Board} Current board situation
	 * @param start {@link Square} Starting position of the movement
	 * @return {@link List}<{@link Square}> List of squares the piece can
	 *         potentially be moved to horizontally
	 */
	final List<Square> getPotentialHorizontalMoves(Board board, Square start) {
		List<Square> moves = new ArrayList<>();
		Square[][] squares = board.getSquares();

		int startRow = start.getRow();
		int startCol = start.getCol();
		
		for(int row = 0; row < 8; row++) {
			if(row != startRow) {
				moves.add(squares[row][startCol]);
			}
		}
		return moves;
	}
	
	/**
	 * Obtains all the squares that it might be possible for the piece to move to vertically.
	 * <strong>This method does not evaluate whether movements to these squares are legal.</strong>
	 * 
	 * @param board {@link Board} Current board situation
	 * @param start {@link Square} Starting position of the movement
	 * @return {@link List}<{@link Square}> List of squares the piece can
	 *         potentially be moved to vertically
	 */
	final List<Square> getPotentialVerticalMoves(Board board, Square start) {
		List<Square> moves = new ArrayList<>();
		Square[][] squares = board.getSquares();

		int startRow = start.getRow();
		int startCol = start.getCol();
		
		for(int col = 0; col < 8; col++) {
			if(col != startCol) {
				moves.add(squares[startRow][col]);
			}
		}
		return moves;
	}
	
	/**
	 * Obtains all the squares that it might be possible for the piece to move to diagonally.
	 * <strong>This method does not evaluate whether movements to these squares are legal.</strong>
	 * 
	 * @param board     {@link Board} Current board situation
	 * @param start     {@link Square} Starting position of the movement
	 * @param direction {@link MovementDirection} Diagonal direction to evaluate
	 * @return {@link List}<{@link Square}> List of squares the piece can
	 *         potentially be moved to diagonally
	 */
	final List<Square> getPotentialDiagonalMoves(Board board, Square start, MovementDirection direction) {
		List<Square> moves = new ArrayList<>();
		Square[][] squares = board.getSquares();

		int startRow = start.getRow();
		int startCol = start.getCol();

		switch(direction) {
		case UP_LEFT:
			for(int row = (startRow + 1), col = (startCol - 1); row < 8 && col >= 0; row++, col--) {
				moves.add(squares[row][col]);
			}
			break;
		case UP_RIGHT:
			for(int row = (startRow + 1), col = (startCol + 1); row < 8 && col < 8; row++, col++) {
				moves.add(squares[row][col]);
			}
			break;
		case DOWN_LEFT:
			for(int row = (startRow - 1), col = (startCol - 1); row >= 0 && col >= 0; row--, col--) {
				moves.add(squares[row][col]);
			}
			break;
		case DOWN_RIGHT:
			for(int row = (startRow - 1), col = (startCol + 1); row >= 0 && col < 8; row--, col++) {
				moves.add(squares[row][col]);
			}
			break;
		default:
			break;
		}

		return moves;
	}

}
