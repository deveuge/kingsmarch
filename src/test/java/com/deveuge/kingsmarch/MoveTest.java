package com.deveuge.kingsmarch;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.deveuge.kingsmarch.engine.Board;
import com.deveuge.kingsmarch.engine.Move;
import com.deveuge.kingsmarch.engine.Player;
import com.deveuge.kingsmarch.engine.pieces.King;
import com.deveuge.kingsmarch.engine.pieces.Pawn;
import com.deveuge.kingsmarch.engine.pieces.Rook;
import com.deveuge.kingsmarch.engine.types.Colour;

@SpringBootTest
class MoveTest {
	
	private Board board = new Board();
	private Player player = new Player(Colour.WHITE, false);
	
	@BeforeEach
	void init() {
		board.empty();
	}
	
	@Test
	void testPawnMove() {
		board.init();
		
		// Advance pawn one square
		Move move = createMove(1, 3, 2, 3);
		assertTrue(move.getPieceMoved() instanceof Pawn);
		assertNull(move.getPieceKilled());
		assertFalse(move.isCastlingMove());
		assertFalse(move.isEnPassant());
		assertFalse(move.isCapturableEnPassant());
		
		// Advance pawn two squares
		Move move2 = createMove(1, 3, 3, 3);
		assertTrue(move2.getPieceMoved() instanceof Pawn);
		assertNull(move2.getPieceKilled());
		assertFalse(move2.isCastlingMove());
		assertFalse(move2.isEnPassant());
		assertTrue(move2.isCapturableEnPassant());
		
		// Advance rook two squares
		board.getSquare(1, 0).getPiece().setFirstMove(false);
		Move move3 = createMove(1, 0, 3, 0);
		assertTrue(move3.getPieceMoved() instanceof Pawn);
		assertNull(move3.getPieceKilled());
		assertFalse(move3.isCastlingMove());
		assertFalse(move3.isEnPassant());
		assertFalse(move3.isCapturableEnPassant());
	}
	
	@Test
	void testCaptureMove() {
		// Simple capture
		board.getSquare(3, 3).setPiece(new King());
		board.getSquare(2, 4).setPiece(new Pawn(Colour.BLACK));
		Move move = createMove(3, 3, 2, 4);
		assertTrue(move.getPieceMoved() instanceof King && move.getPieceMoved().isWhite());
		assertTrue(move.getPieceKilled() instanceof Pawn && !move.getPieceKilled().isWhite());
		assertFalse(move.isCastlingMove());
		assertFalse(move.isEnPassant());
		assertFalse(move.isCapturableEnPassant());
		
		// En passant capture
		board.getSquare(4, 5).setPiece(new Pawn());
		board.getSquare(4, 6).setPiece(new Pawn(Colour.BLACK));
		board.getSquare(4, 6).getPiece().setFirstMove(false);
		((Pawn) board.getSquare(4, 6).getPiece()).setCapturableEnPassant(true);
		Move move2 = createMove(4, 5, 5, 6);
		assertTrue(move2.getPieceMoved() instanceof Pawn && move.getPieceMoved().isWhite());
		assertTrue(move2.getPieceKilled() instanceof Pawn && !move.getPieceKilled().isWhite());
		assertFalse(move2.isCastlingMove());
		assertTrue(move2.isEnPassant());
		assertFalse(move2.isCapturableEnPassant());
	}
	
	@Test
	void testCastlingMove() {
		board.getSquare(0, 4).setPiece(new King());
		board.getSquare(0, 7).setPiece(new Rook());
		
		Move move = createMove(0, 4, 0, 7);
		assertTrue(move.getPieceMoved() instanceof King && move.getPieceMoved().isWhite());
		assertNull(move.getPieceKilled());
		assertTrue(move.isCastlingMove());
		assertFalse(move.isEnPassant());
		assertFalse(move.isCapturableEnPassant());
		
	}

	private Move createMove(int startRow, int startCol, int endRow, int endCol) {
		return new Move(player, board.getSquare(startRow, startCol), board.getSquare(endRow, endCol), board);
	}
}
