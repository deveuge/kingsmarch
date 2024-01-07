package com.deveuge.kingsmarch.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.deveuge.kingsmarch.engine.pieces.Bishop;
import com.deveuge.kingsmarch.engine.pieces.King;
import com.deveuge.kingsmarch.engine.pieces.Knight;
import com.deveuge.kingsmarch.engine.pieces.Pawn;
import com.deveuge.kingsmarch.engine.pieces.Piece;
import com.deveuge.kingsmarch.engine.pieces.Queen;
import com.deveuge.kingsmarch.engine.pieces.Rook;
import com.deveuge.kingsmarch.engine.types.Colour;

import lombok.Getter;

@Getter
public class Board {

	Square[][] squares;

	public Board() {
		this.init();
	}
	
	/**
	 * Initialises the board from a FEN string.
	 * 
	 * @param fen {@link String} FEN representing the current state of the board
	 */
	public Board(String fen) {
		squares = new Square[8][8];
		String[] rows = fen.split("/");
		
		// Read FEN String
		int colIndex;
        int rowIndex = 7;
        for (String row : rows) {
            colIndex = 0;
            for (int i = 0; i < row.length(); i++) {
                char character = row.charAt(i);
                if (Character.isDigit(character)) {
                    colIndex += Character.digit(character, 10);
                } else {
                	Piece piece = Piece.createFromAlgebraicNotation(String.valueOf(character));
                	piece.setFirstMove(false); // TODO
                	Square sq = new Square(rowIndex, colIndex, piece);
                	squares[rowIndex][colIndex] = sq;
                    colIndex++;
                }
            }
            rowIndex--;
        }
        
        // Empty squares
        for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				if(squares[row][col] == null)
				squares[row][col] = new Square(row, col, null);
			}
		}
	}
	
	/**
	 * Constructor that makes a deep copy (except for pieces) of the current
	 * situation of a board.
	 * 
	 * @param board {@link Board} Current board state
	 */
	public Board(Board board) {
		squares = new Square[8][8];
		for(int row = 0; row < board.getSquares().length; row++) {
			for(int col = 0; col < board.getSquares()[row].length; col++) {
				Square square = board.getSquares()[row][col];
				Square clonedSquare = new Square(square.getRow(), square.getCol(), square.getPiece());
				this.squares[row][col] = clonedSquare;
			}
		}
	}

	/**
	 * Gets a specific square on the board
	 * 
	 * @param row Index of the row
	 * @param col Index of the column
	 * @return {@link Square}
	 */
	public Square getSquare(int row, int col) {
		try {
			return squares[row][col];
		} catch(ArrayIndexOutOfBoundsException ex) {
			return null;
		}
	}
	
	/**
	 * Gets the square where the king of a certain colour is located.
	 * 
	 * @param colour {@link Colour} Colour of the king to be searched
	 * @return {@link Square} Square occupied by the king
	 */
	public Square getKingSquare(Colour colour) {
		for (Square[] rows : squares) {
			for (Square square : rows) {
				if (square.isOccupied() && square.getPiece() instanceof King
						&& square.getPiece().getColour().equals(colour)) {
					return square;
				}
			}
		}
		return null;
	}
	
	/**
	 * Gets all the squares on the board that are occupied by a piece of the colour
	 * passed by parameter.
	 * 
	 * @param colour {@link Colour} Colour of the pieces to be searched
	 * @return {@link List}<{@link Square}> List of squares occupied by that colour
	 */
	public List<Square> getOccupiedSquares(Colour colour) {
		List<Square> occupiedSquares = new ArrayList<>();
		for (Square[] rows : squares) {
		    for (Square square : rows) {
		        if(square.isOccupied() && colour.equals(square.getPiece().getColour())) {
		        	occupiedSquares.add(square);
		        }
 		    }
		}
		return occupiedSquares;
	}

	/**
	 * Initialises the board with the pieces placed in their default position.
	 */
	public void init() {
		squares = new Square[8][8];
		List<Piece> whitePieces = Arrays.asList(
				new Rook(), new Knight(), new Bishop(),
				new Queen(), new King(),
				new Bishop(), new Knight(), new Rook());
		List<Piece> blackPieces = Arrays.asList(
				new Rook(), new Knight(), new Bishop(),
				new Queen(), new King(),
				new Bishop(), new Knight(), new Rook());
		blackPieces.forEach(blackPiece -> blackPiece.setColour(Colour.BLACK));
		
		for(int col = 0; col < whitePieces.size(); col++) {
			squares[0][col] = new Square(0, col, whitePieces.get(col));
			squares[1][col] = new Square(1, col, new Pawn());
			squares[6][col] = new Square(6, col, new Pawn(Colour.BLACK));
			squares[7][col] = new Square(7, col, blackPieces.get(col));
		}

		// Initialize remaining squares without any piece
		for (int row = 2; row < 6; row++) {
			for (int col = 0; col < 8; col++) {
				squares[row][col] = new Square(row, col, null);
			}
		}
	}
	
	/**
	 * Initialises the board without pieces.
	 */
	public void empty() {
		squares = new Square[8][8];
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				squares[row][col] = new Square(row, col, null);
			}
		}
	}

	/**
	 * Obtains the Forsyth-Edwards Notation of the pieces taking into account the
	 * current state of the game. Only piece placement data is included.
	 * 
	 * @return {@link String} FEN record with the current piece placement data
	 */
	public String getFEN() {
		StringBuilder sb = new StringBuilder();
		for (int row = 7; row >= 0; row--) {
			int emptySquares = 0;
			for (int col = 0; col < 8; col++) {
				Square square = squares[row][col];
				if(square.isOccupied()) {
					if(emptySquares != 0) {
						sb.append(emptySquares);
						emptySquares = 0;
					}
					String pieceNotation = square.getPiece().getAlgebraicNotation();
					sb.append(square.getPiece().isWhite() ? pieceNotation : pieceNotation.toLowerCase());
				} else {
					emptySquares++;
				}
			}
			if(emptySquares != 0) {
				sb.append(emptySquares);
				emptySquares = 0;
			}
			sb.append("/");
		}
		
		String fen = sb.toString();
		return fen.substring(0, fen.length() - 1);
	}
}
