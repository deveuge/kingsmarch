package com.deveuge.kingsmarch;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.deveuge.kingsmarch.engine.Board;
import com.deveuge.kingsmarch.engine.Square;
import com.deveuge.kingsmarch.engine.pieces.Bishop;
import com.deveuge.kingsmarch.engine.pieces.King;
import com.deveuge.kingsmarch.engine.pieces.Knight;
import com.deveuge.kingsmarch.engine.pieces.Pawn;
import com.deveuge.kingsmarch.engine.pieces.Piece;
import com.deveuge.kingsmarch.engine.pieces.Queen;
import com.deveuge.kingsmarch.engine.pieces.Rook;
import com.deveuge.kingsmarch.engine.types.Colour;

@SpringBootTest
class PiecesTest {
	
	private static final List<Class<? extends Piece>> PIECES_ALIGNMENT = Arrays.asList(
			Rook.class, Knight.class, Bishop.class,
			Queen.class, King.class,
			Bishop.class, Knight.class, Rook.class);
	
	private Board board = new Board();
	
	@BeforeEach
	void init() {
		board.empty();
	}
	
	@Test
	void checkInitialPiecesPositions() {
		board.init();
		for(int i = 0; i < 8; i++) {
			assertTrue(isCorrectPiece(0, i, PIECES_ALIGNMENT.get(i), true));
			assertTrue(isCorrectPiece(1, i, Pawn.class, true));
			assertTrue(isCorrectPiece(6, i, Pawn.class, false));
			assertTrue(isCorrectPiece(7, i, PIECES_ALIGNMENT.get(i), false));
		}
		for (int i = 2; i < 6; i++) {
			for (int j = 0; j < 8; j++) {
				assertTrue(!board.getSquare(i, j).isOccupied());
			}
		}
	}

	@Test
	void cannotMovePiecesInitialState() {
		board.init();
		for(int i = 0; i < 8; i++) {
			assertFalse(checkIfCanMove(0, i, 1, i));
			assertFalse(checkIfCanMove(7, i, 6, i));
		}
	}
	
	@Test
	void pawnMovement() {
		board.getSquare(1, 0).setPiece(new Pawn());
		board.getSquare(1, 1).setPiece(new Pawn());
		// Same position
		assertFalse(checkIfCanMove(1, 0, 1, 0));
		// Advance one square
		assertTrue(checkIfCanMove(1, 0, 2, 0));
		// Advance two squares (first move)
		assertTrue(checkIfCanMove(1, 0, 3, 0));
		// Advance three squares
		assertFalse(checkIfCanMove(1, 0, 4, 0));
		// Advance diagonally
		assertFalse(checkIfCanMove(1, 0, 2, 1));
		// Advance two squares (not first move)
		board.getSquare(1, 0).getPiece().setFirstMove(false);
		assertFalse(checkIfCanMove(1, 0, 3, 0));
		// Advance horizontally
		assertFalse(checkIfCanMove(1, 0, 1, 1));
		// Advance diagonally (w/o opponent piece)
		assertFalse(checkIfCanMove(1, 1, 2, 0));
		assertFalse(checkIfCanMove(1, 1, 2, 2));
		// Advance diagonally (w/ opponent piece)
		board.getSquare(2, 0).setPiece(new Pawn(Colour.BLACK));
		board.getSquare(2, 2).setPiece(new Pawn(Colour.BLACK));
		board.getSquare(1, 1).getPiece().setFirstMove(false);
		assertTrue(checkIfCanMove(1, 1, 2, 0));
		assertTrue(checkIfCanMove(1, 1, 2, 2));
		// Advance vertically (w/ opponent piece)
		assertFalse(checkIfCanMove(1, 0, 2, 0));
		
		// Advance black pawn
		board.getSquare(7, 0).setPiece(new Pawn(Colour.BLACK));
		assertTrue(checkIfCanMove(7, 0, 6, 0));
		board.getSquare(7, 0).getPiece().setFirstMove(false);
		assertFalse(checkIfCanMove(7, 0, 6, 1));
	}
	
	@Test
	void rookMovement() {
		board.getSquare(0, 0).setPiece(new Rook());
		board.getSquare(1, 0).setPiece(new Pawn());
		board.getSquare(0, 1).setPiece(new Pawn());
		
		// Same position
		assertFalse(checkIfCanMove(0, 0, 1, 0));
		// Move diagonally
		assertFalse(checkIfCanMove(0, 0, 2, 2));
		
		// Move up (w/ own piece)
		assertFalse(checkIfCanMove(0, 0, 1, 0));
		// Move up (w/o own piece)
		board.getSquare(1, 0).setPiece(null);
		assertTrue(checkIfCanMove(0, 0, 5, 0));
		// Move up (w/ own piece in the way)
		board.getSquare(1, 0).setPiece(new Pawn());
		assertFalse(checkIfCanMove(0, 0, 5, 0));
		board.getSquare(1, 0).setPiece(null);

		// Move right (w/ own piece)
		assertFalse(checkIfCanMove(0, 0, 0, 1));
		// Move right (w/o own piece)
		board.getSquare(0, 1).setPiece(null);
		assertTrue(checkIfCanMove(0, 0, 0, 1));
		// Move right (w/ opponent piece)
		board.getSquare(0, 7).setPiece(new Pawn(Colour.BLACK));
		assertTrue(checkIfCanMove(0, 0, 0, 7));
		board.getSquare(0, 7).setPiece(null);
		// Move right (w/ own piece in the way)
		board.getSquare(0, 3).setPiece(new Pawn());
		assertFalse(checkIfCanMove(0, 0, 0, 5));
		board.getSquare(0, 3).setPiece(null);


		board.getSquare(0, 0).setPiece(null);
		board.getSquare(7, 7).setPiece(new Rook());
		board.getSquare(7, 6).setPiece(new Pawn());
		// Move left (w/ own piece)
		assertFalse(checkIfCanMove(7, 7, 7, 1));
		// Move left (w/o own piece)
		board.getSquare(7, 6).setPiece(null);
		assertTrue(checkIfCanMove(7, 7, 7, 1));
		// Move left (w/ opponent piece)
		board.getSquare(7, 6).setPiece(new Pawn(Colour.BLACK));
		assertTrue(checkIfCanMove(7, 7, 7, 6));
		board.getSquare(7, 6).setPiece(null);
		// Move left (w/ own piece in the way)
		board.getSquare(7, 3).setPiece(new Pawn());
		assertFalse(checkIfCanMove(7, 7, 7, 0));
		board.getSquare(7, 3).setPiece(null);
		
		// Move down (w/ own piece)
		board.getSquare(6, 7).setPiece(new Pawn());
		assertFalse(checkIfCanMove(7, 7, 1, 7));
		// Move down (w/o own piece)
		board.getSquare(6, 7).setPiece(null);
		assertTrue(checkIfCanMove(7, 7, 1, 7));
		// Move down (w/ opponent piece)
		board.getSquare(6, 7).setPiece(new Pawn(Colour.BLACK));
		assertTrue(checkIfCanMove(7, 7, 6, 7));
		board.getSquare(6, 7).setPiece(null);
		// Move down (w/ own piece in the way)
		board.getSquare(6, 3).setPiece(new Pawn());
		assertFalse(checkIfCanMove(7, 7, 6, 0));
		board.getSquare(6, 3).setPiece(null);
	}
	
	@Test
	void knightMovement() {
		board.getSquare(3, 3).setPiece(new Knight());
		// Move all directions
		assertTrue(checkIfCanMove(3, 3, 4, 5));
		assertTrue(checkIfCanMove(3, 3, 2, 5));
		assertTrue(checkIfCanMove(3, 3, 2, 1));
		assertTrue(checkIfCanMove(3, 3, 4, 1));
		assertTrue(checkIfCanMove(3, 3, 5, 2));
		assertTrue(checkIfCanMove(3, 3, 1, 2));
		assertTrue(checkIfCanMove(3, 3, 4, 1));
		assertTrue(checkIfCanMove(3, 3, 5, 4));
		assertFalse(checkIfCanMove(3, 3, 3, 3));
		assertFalse(checkIfCanMove(3, 3, 3, 5));
		assertFalse(checkIfCanMove(3, 3, 5, 3));
		// Move all directions (w/ piece in the way)
		board.getSquare(4, 4).setPiece(new Pawn());
		assertTrue(checkIfCanMove(3, 3, 4, 5));
		// Move all directions (w/ opponent piece)
		board.getSquare(4, 5).setPiece(new Pawn(Colour.BLACK));
		assertTrue(checkIfCanMove(3, 3, 4, 5));
		// Move all directions (w/ own piece)
		board.getSquare(4, 5).setPiece(new Pawn());
		assertFalse(checkIfCanMove(3, 3, 4, 5));
	}
	
	@Test
	void bishopMovement() {
		board.getSquare(3, 3).setPiece(new Bishop());
		// Move all directions
		assertTrue(checkIfCanMove(3, 3, 5, 5));
		assertTrue(checkIfCanMove(3, 3, 5, 1));
		assertTrue(checkIfCanMove(3, 3, 1, 1));
		assertTrue(checkIfCanMove(3, 3, 1, 5));
		assertFalse(checkIfCanMove(3, 3, 3, 3));
		assertFalse(checkIfCanMove(3, 3, 3, 5));
		assertFalse(checkIfCanMove(3, 3, 5, 3));
		// Move all directions (w/ piece in the way)
		board.getSquare(4, 4).setPiece(new Pawn());
		assertFalse(checkIfCanMove(3, 3, 5, 5));
		board.getSquare(4, 4).setPiece(null);
		board.getSquare(4, 2).setPiece(new Pawn());
		assertFalse(checkIfCanMove(3, 3, 5, 1));
		board.getSquare(4, 2).setPiece(null);
		board.getSquare(2, 4).setPiece(new Pawn());
		assertFalse(checkIfCanMove(3, 3, 1, 5));
		board.getSquare(2, 4).setPiece(null);
		board.getSquare(2, 2).setPiece(new Pawn());
		assertFalse(checkIfCanMove(3, 3, 1, 1));
		board.getSquare(2, 2).setPiece(null);
		// Move all directions (w/ opponent piece)
		board.getSquare(5, 5).setPiece(new Pawn(Colour.BLACK));
		assertTrue(checkIfCanMove(3, 3, 5, 5));
		board.getSquare(5, 5).setPiece(null);
		board.getSquare(5, 1).setPiece(new Pawn(Colour.BLACK));
		assertTrue(checkIfCanMove(3, 3, 5, 1));
		board.getSquare(5, 1).setPiece(null);
		board.getSquare(1, 5).setPiece(new Pawn(Colour.BLACK));
		assertTrue(checkIfCanMove(3, 3, 1, 5));
		board.getSquare(1, 5).setPiece(null);
		board.getSquare(1, 1).setPiece(new Pawn(Colour.BLACK));
		assertTrue(checkIfCanMove(3, 3, 1, 1));
		board.getSquare(1, 1).setPiece(null);
		// Move all directions (w/ own piece)
		board.getSquare(5, 5).setPiece(new Pawn());
		assertFalse(checkIfCanMove(3, 3, 5, 5));
		board.getSquare(5, 5).setPiece(null);
		board.getSquare(5, 1).setPiece(new Pawn());
		assertFalse(checkIfCanMove(3, 3, 5, 1));
		board.getSquare(5, 1).setPiece(null);
		board.getSquare(1, 5).setPiece(new Pawn());
		assertFalse(checkIfCanMove(3, 3, 1, 5));
		board.getSquare(1, 5).setPiece(null);
		board.getSquare(1, 1).setPiece(new Pawn());
		assertFalse(checkIfCanMove(3, 3, 1, 1));
		board.getSquare(1, 1).setPiece(null);
	}
	
	@Test
	void queenMovement() {
		board.getSquare(3, 3).setPiece(new Queen());
		// Move all directions
		assertTrue(checkIfCanMove(3, 3, 5, 5));
		assertTrue(checkIfCanMove(3, 3, 5, 1));
		assertTrue(checkIfCanMove(3, 3, 1, 1));
		assertTrue(checkIfCanMove(3, 3, 1, 5));
		assertTrue(checkIfCanMove(3, 3, 3, 5));
		assertTrue(checkIfCanMove(3, 3, 5, 3));
		assertTrue(checkIfCanMove(3, 3, 3, 1));
		assertTrue(checkIfCanMove(3, 3, 1, 3));
		assertFalse(checkIfCanMove(3, 3, 3, 3));
		assertFalse(checkIfCanMove(3, 3, 4, 5));
		
		// Move all directions (w/ piece in the way)
		board.getSquare(4, 4).setPiece(new Pawn());
		board.getSquare(4, 2).setPiece(new Pawn());
		board.getSquare(2, 4).setPiece(new Pawn());
		board.getSquare(2, 2).setPiece(new Pawn());
		assertFalse(checkIfCanMove(3, 3, 5, 5));
		assertFalse(checkIfCanMove(3, 3, 5, 1));
		assertFalse(checkIfCanMove(3, 3, 1, 1));
		assertFalse(checkIfCanMove(3, 3, 1, 5));
		board.getSquare(3, 4).setPiece(new Pawn());
		board.getSquare(4, 3).setPiece(new Pawn());
		board.getSquare(3, 2).setPiece(new Pawn());
		board.getSquare(2, 3).setPiece(new Pawn());
		assertFalse(checkIfCanMove(3, 3, 3, 5));
		assertFalse(checkIfCanMove(3, 3, 5, 3));
		assertFalse(checkIfCanMove(3, 3, 3, 1));
		assertFalse(checkIfCanMove(3, 3, 1, 3));
		
		// Move all directions (w/ own piece)
		assertFalse(checkIfCanMove(3, 3, 4, 4));
		assertFalse(checkIfCanMove(3, 3, 4, 2));
		assertFalse(checkIfCanMove(3, 3, 2, 4));
		assertFalse(checkIfCanMove(3, 3, 2, 2));
		assertFalse(checkIfCanMove(3, 3, 3, 4));
		assertFalse(checkIfCanMove(3, 3, 4, 3));
		assertFalse(checkIfCanMove(3, 3, 3, 2));
		assertFalse(checkIfCanMove(3, 3, 2, 3));

		// Move all directions (w/ opponent piece)
		board.getSquare(4, 4).setPiece(new Pawn(Colour.BLACK));
		board.getSquare(4, 2).setPiece(new Pawn(Colour.BLACK));
		board.getSquare(2, 4).setPiece(new Pawn(Colour.BLACK));
		board.getSquare(2, 2).setPiece(new Pawn(Colour.BLACK));
		board.getSquare(3, 4).setPiece(new Pawn(Colour.BLACK));
		board.getSquare(4, 3).setPiece(new Pawn(Colour.BLACK));
		board.getSquare(3, 2).setPiece(new Pawn(Colour.BLACK));
		board.getSquare(2, 3).setPiece(new Pawn(Colour.BLACK));
		assertTrue(checkIfCanMove(3, 3, 4, 4));
		assertTrue(checkIfCanMove(3, 3, 4, 2));
		assertTrue(checkIfCanMove(3, 3, 2, 4));
		assertTrue(checkIfCanMove(3, 3, 2, 2));
		assertTrue(checkIfCanMove(3, 3, 3, 4));
		assertTrue(checkIfCanMove(3, 3, 4, 3));
		assertTrue(checkIfCanMove(3, 3, 3, 2));
		assertTrue(checkIfCanMove(3, 3, 2, 3));
	}
	
	@Test
	void kingMovement() {
		board.getSquare(3, 3).setPiece(new King());
		// Move all directions
		assertTrue(checkIfCanMove(3, 3, 3, 4));
		assertTrue(checkIfCanMove(3, 3, 3, 2));
		assertTrue(checkIfCanMove(3, 3, 4, 3));
		assertTrue(checkIfCanMove(3, 3, 2, 3));
		
		assertTrue(checkIfCanMove(3, 3, 4, 4));
		assertTrue(checkIfCanMove(3, 3, 4, 2));
		assertTrue(checkIfCanMove(3, 3, 2, 2));
		assertTrue(checkIfCanMove(3, 3, 2, 4));
		
		assertFalse(checkIfCanMove(3, 3, 3, 3));
		assertFalse(checkIfCanMove(3, 3, 4, 5));
		assertFalse(checkIfCanMove(3, 3, 5, 5));
		
		// In check
		board.getSquare(1, 5).setPiece(new Knight());
		board.getSquare(1, 5).getPiece().setColour(Colour.BLACK);
		assertFalse(checkIfCanMove(3, 3, 3, 4));
		board.getSquare(1, 5).setPiece(null);
		
		// Move all directions (w/ own piece)
		board.getSquare(3, 4).setPiece(new Pawn());
		board.getSquare(3, 2).setPiece(new Pawn());
		board.getSquare(4, 3).setPiece(new Pawn());
		board.getSquare(2, 3).setPiece(new Pawn());
		assertFalse(checkIfCanMove(3, 3, 3, 4));
		assertFalse(checkIfCanMove(3, 3, 3, 2));
		assertFalse(checkIfCanMove(3, 3, 4, 3));
		assertFalse(checkIfCanMove(3, 3, 2, 3));
		board.getSquare(4, 4).setPiece(new Pawn());
		board.getSquare(4, 2).setPiece(new Pawn());
		board.getSquare(2, 2).setPiece(new Pawn());
		board.getSquare(2, 4).setPiece(new Pawn());
		assertFalse(checkIfCanMove(3, 3, 4, 4));
		assertFalse(checkIfCanMove(3, 3, 4, 2));
		assertFalse(checkIfCanMove(3, 3, 2, 2));
		assertFalse(checkIfCanMove(3, 3, 2, 4));
		
		// Move all directions (w/ opponent piece)
		board.getSquare(3, 4).setPiece(new Pawn(Colour.BLACK));
		board.getSquare(3, 2).setPiece(new Pawn(Colour.BLACK));
		board.getSquare(4, 3).setPiece(new Pawn(Colour.BLACK));
		board.getSquare(2, 3).setPiece(new Pawn(Colour.BLACK));
		assertTrue(checkIfCanMove(3, 3, 3, 4));
		assertTrue(checkIfCanMove(3, 3, 3, 2));
		assertTrue(checkIfCanMove(3, 3, 4, 3));
		assertTrue(checkIfCanMove(3, 3, 2, 3));
		board.getSquare(4, 4).setPiece(new Pawn(Colour.BLACK));
		board.getSquare(4, 2).setPiece(new Pawn(Colour.BLACK));
		board.getSquare(2, 2).setPiece(new Pawn(Colour.BLACK));
		board.getSquare(2, 4).setPiece(new Pawn(Colour.BLACK));
		assertTrue(checkIfCanMove(3, 3, 4, 4));
		assertTrue(checkIfCanMove(3, 3, 4, 2));
		assertTrue(checkIfCanMove(3, 3, 2, 2));
		assertTrue(checkIfCanMove(3, 3, 2, 4));
	}
	
	@Test
	void kingCastlingMovement() {
		board.getSquare(0, 4).setPiece(new King());
		board.getSquare(0, 0).setPiece(new Rook());
		board.getSquare(0, 7).setPiece(new Rook());

		// Correct castling
		assertTrue(checkIfCanMove(0, 4, 0, 0));
		assertTrue(checkIfCanMove(0, 4, 0, 7));

		// Incorrect castling (rook in incorrect position)
		board.getSquare(1, 4).setPiece(new Rook());
		assertFalse(checkIfCanMove(0, 4, 1, 4));
		board.getSquare(1, 4).setPiece(null);
		
		// Incorrect castling (king has moved)
		board.getSquare(0, 4).getPiece().setFirstMove(false);
		assertFalse(checkIfCanMove(0, 4, 0, 0));
		assertFalse(checkIfCanMove(0, 4, 0, 7));
		
		// Incorrect castling (rook have moved)
		board.getSquare(0, 4).getPiece().setFirstMove(true);
		board.getSquare(0, 0).getPiece().setFirstMove(false);
		board.getSquare(0, 7).getPiece().setFirstMove(false);
		assertFalse(checkIfCanMove(0, 4, 0, 0));
		assertFalse(checkIfCanMove(0, 4, 0, 7));
		
		// Incorrect castling (pieces of wrong colour)
		board.getSquare(0, 0).getPiece().setFirstMove(true);
		board.getSquare(0, 7).getPiece().setFirstMove(true);
		board.getSquare(0, 0).getPiece().setColour(Colour.BLACK);
		board.getSquare(0, 7).getPiece().setColour(Colour.BLACK);
		assertFalse(checkIfCanMove(0, 4, 0, 0));
		assertFalse(checkIfCanMove(0, 4, 0, 7));

		// Incorrect castling (there is no rook)
		board.getSquare(0, 0).setPiece(null);
		board.getSquare(0, 7).setPiece(null);
		assertFalse(checkIfCanMove(0, 4, 0, 0));
		assertFalse(checkIfCanMove(0, 4, 0, 7));
		
		// Incorrect castling (incorrect pieces)
		board.getSquare(0, 0).setPiece(new Pawn());
		board.getSquare(0, 7).setPiece(new Pawn(Colour.BLACK));
		assertFalse(checkIfCanMove(0, 4, 0, 0));
		assertFalse(checkIfCanMove(0, 4, 0, 7));
		
		// Incorrect castling (rook in incorrect position)
		board.getSquare(1, 4).setPiece(new Rook());
		board.getSquare(1, 4).getPiece().setFirstMove(false);
		assertFalse(checkIfCanMove(0, 4, 1, 4));
		board.getSquare(1, 4).setPiece(null);
	}
	
	@Test
	void kingCastlingCheckMovement() {
		board.getSquare(0, 4).setPiece(new King());
		board.getSquare(0, 0).setPiece(new Rook());
		board.getSquare(0, 7).setPiece(new Rook());
		
		// Incorrect castling (king in check)
		board.getSquare(1, 3).setPiece(new Pawn(Colour.BLACK));
		board.getSquare(1, 3).getPiece().setFirstMove(false);
		assertFalse(checkIfCanMove(0, 4, 0, 0));
		assertFalse(checkIfCanMove(0, 4, 0, 7));
		
		// Correct castling
		board.getSquare(1, 3).setPiece(new Pawn());
		assertTrue(checkIfCanMove(0, 4, 0, 0));
		assertTrue(checkIfCanMove(0, 4, 0, 7));
		board.getSquare(1, 3).setPiece(null);
		
		// Incorrect castling (king goes through check)
		board.getSquare(7, 1).setPiece(new Queen());
		board.getSquare(7, 1).getPiece().setColour(Colour.BLACK);
		assertFalse(checkIfCanMove(0, 4, 0, 0));
		board.getSquare(7, 1).setPiece(null);

		// Incorrect castling (king goes through check)
		board.getSquare(4, 1).setPiece(new Knight());
		board.getSquare(4, 1).getPiece().setColour(Colour.BLACK);
		assertFalse(checkIfCanMove(0, 4, 0, 0));
		board.getSquare(4, 1).setPiece(null);
		
	}
	
	private <T extends Piece> boolean isCorrectPiece(int col, int row, Class<T> clazz, boolean white) {
		Piece piece = board.getSquare(col, row).getPiece();
		return piece.getClass().equals(clazz) && piece.isWhite() == white;
	}
	
	private boolean checkIfCanMove(int startRow, int startCol, int endRow, int endCol) {
		Square start = board.getSquare(startRow, startCol);
		Square end = board.getSquare(endRow, endCol);
		Piece piece = start.getPiece();
		return piece.canMove(board, start, end);
	}

}
