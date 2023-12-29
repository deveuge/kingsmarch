package com.deveuge.kingsmarch.engine;

import java.util.ArrayList;
import java.util.List;

import com.deveuge.kingsmarch.engine.pieces.King;
import com.deveuge.kingsmarch.engine.pieces.Pawn;
import com.deveuge.kingsmarch.engine.pieces.Piece;
import com.deveuge.kingsmarch.engine.types.CastlingDirection;
import com.deveuge.kingsmarch.engine.types.GameStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Game {

	private Player[] players = new Player[2];
	private Board board;
	private Player currentTurn;
	private GameStatus status;
	private List<Move> movesPlayed = new ArrayList<>();

	public void init(Player p1, Player p2) {
		players[0] = p1;
		players[1] = p2;
		this.currentTurn = p1.isWhiteSide() ? p1 : p2;

		board.init();
		movesPlayed.clear();
	}

	public boolean isEnd() {
		return status.isEndOfGame();
	}

	public boolean playerMove(Player player, int startRow, int startCol, int endRow, int endCol) {
		Square start = board.getSquare(startRow, startCol);
		Square end = board.getSquare(endRow, endCol);
		Move move = new Move(player, start, end, board);
		return this.makeMove(move);
	}

	private boolean makeMove(Move move) {
		Player player = move.getPlayer();
		Piece sourcePiece = move.getStart().getPiece();
		Piece destPiece = move.getEnd().getPiece();
		
		if (!isMoveAllowedForPlayer(player, sourcePiece) || !isMoveAllowedForPiece(move)) {
			return false;
		}
		
		if (move.isCastlingMove()) {
			makeRookCastlingMove(move);
		}

		performMove(move);
		updateGameStatus(player, destPiece);
		updatePlayerCurrentTurn();
		return true;
	}
	
	private boolean isMoveAllowedForPlayer(Player player, Piece sourcePiece) {
		return player == currentTurn && player.isWhiteSide() == sourcePiece.isWhite();
	}
	
	private boolean isMoveAllowedForPiece(Move move) {
		Piece sourcePiece = move.getStart().getPiece();
		return sourcePiece != null && sourcePiece.canMove(board, move.getStart(), move.getEnd());
	}
	
	private void makeRookCastlingMove(Move move) {
		CastlingDirection direction = CastlingDirection.get(move.getEnd().getCol());
		move.getEnd().setCol(direction.getEndingKingCol());
		int row = move.getStart().getRow();
		Square rookSquare = board.getSquare(row, direction.getCol());
		board.getSquare(row, direction.getEndingRookCol()).setPiece(rookSquare.getPiece());
		rookSquare.setPiece(null);
	}
	
	private void performMove(Move move) {
		Piece sourcePiece = move.getStart().getPiece();
		
		if(sourcePiece instanceof Pawn) {
			((Pawn) sourcePiece).setCapturableEnPassant(move.isCapturableEnPassant());
		}
		
		if(move.isEnPassant()) {
			move.getEnPassantCaptureSquare().setPiece(null);
		}
		
		sourcePiece.setFirstMove(false);
		movesPlayed.add(move);
		move.getEnd().setPiece(move.getStart().getPiece());
		move.getStart().setPiece(null);
	}
	
	private void updateGameStatus(Player player, Piece destPiece) {
		if (destPiece != null && destPiece instanceof King) {
			this.setStatus(player.isWhiteSide() ? GameStatus.WHITE_WIN : GameStatus.BLACK_WIN);
		}
		// TODO
	}
	
	private void updatePlayerCurrentTurn() {
		this.currentTurn = this.currentTurn == players[0] ? players[1] : players[0];
	}
}
