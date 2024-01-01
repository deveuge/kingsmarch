package com.deveuge.kingsmarch.engine;

import java.util.ArrayList;
import java.util.List;

import com.deveuge.kingsmarch.engine.pieces.King;
import com.deveuge.kingsmarch.engine.pieces.Pawn;
import com.deveuge.kingsmarch.engine.pieces.Piece;
import com.deveuge.kingsmarch.engine.types.Colour;
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

	public Game() {
		super();
		players[0] = new Player(Colour.WHITE, false);
		players[1] = new Player(Colour.BLACK, true);
		this.init();
	}

	public void init() {
		this.currentTurn = players[0].isWhiteSide() ? players[0] : players[1];
		this.board = new Board();
		movesPlayed.clear();
	}

	public boolean isEnd() {
		return status.isEndOfGame();
	}
	
	public Player getPlayer(Colour colour) {
		return Colour.WHITE.equals(colour) ? players[0] : players[1];
	}

	public boolean playerMove(Player player, Position startPosition, Position endPosition) {
		Square start = board.getSquare(startPosition.getRow(), startPosition.getCol());
		Square end = board.getSquare(endPosition.getRow(), endPosition.getCol());
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
		Square rookSquare = move.getRookCastlingSquare();
		board.getSquare(rookSquare.getRow(), move.getCastlingDirection().getEndingRookCol()).setPiece(rookSquare.getPiece());
		move.getRookCastlingSquare().setPiece(null);
		move.setEnd(board.getSquare(move.getEnd().getRow(), move.getCastlingDirection().getEndingKingCol()));
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
		move.getEnd().setPiece(sourcePiece);
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
	
	public Move getLastMove() {
		return this.movesPlayed.get(this.movesPlayed.size() - 1);
	}
}
