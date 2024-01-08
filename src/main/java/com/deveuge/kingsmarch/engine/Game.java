package com.deveuge.kingsmarch.engine;

import java.util.ArrayList;
import java.util.List;

import com.deveuge.kingsmarch.engine.pieces.Pawn;
import com.deveuge.kingsmarch.engine.pieces.Piece;
import com.deveuge.kingsmarch.engine.types.Colour;
import com.deveuge.kingsmarch.engine.types.GameStatus;
import com.deveuge.kingsmarch.engine.util.Position;

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
		players[0] = new Player(Colour.WHITE);
		players[1] = new Player(Colour.BLACK);
		this.init();
	}

	/**
	 * Initializes the game.
	 */
	public void init() {
		this.currentTurn = players[0].isWhiteSide() ? players[0] : players[1];
		this.board = new Board();
		movesPlayed.clear();
	}

	/**
	 * Performs the movement of a piece.
	 * 
	 * @param player        {@link Player} Player making the move
	 * @param startPosition {@link Position} Initial position
	 * @param endPosition   {@link Position} Ending position
	 * @return true if the movement has been made, false otherwise
	 */
	public boolean move(Player player, Position startPosition, Position endPosition) {
		Square start = board.getSquare(startPosition.getRow(), startPosition.getCol());
		Square end = board.getSquare(endPosition.getRow(), endPosition.getCol());
		if(start.getPiece() == null) {
			return false;
		}
		Move move = new Move(player, start, end, board);
		return this.makeMove(move);
	}

	/**
	 * Performs the movement of a piece. Internal method.
	 * 
	 * @param move {@link Move} Movement data
	 * @return true if the movement has been made, false otherwise
	 */
	private boolean makeMove(Move move) {
		if (!isMoveAllowedForPlayer(move) || !isMoveAllowedForPiece(move)) {
			return false;
		}
		if (move.isCastlingMove()) {
			makeRookCastlingMove(move);
		}
		performMove(move);
		updateGameStatus();
		updatePlayerCurrentTurn();
		return true;
	}
	
	/**
	 * Checks if the player can make a move.
	 * 
	 * @param move {@link Move} Movement data
	 * @return true if the player can make the move, false otherwise
	 */
	private boolean isMoveAllowedForPlayer(Move move) {
		Player player = move.getPlayer();
		Piece sourcePiece = move.getStart().getPiece();
		return player == currentTurn && player.isWhiteSide() == sourcePiece.isWhite();
	}
	
	/**
	 * Checks if the piece can be moved.
	 * 
	 * @param move {@link Move} Movement data
	 * @return true if the piece can be moved, false otherwise
	 */
	private boolean isMoveAllowedForPiece(Move move) {
		Piece sourcePiece = move.getStart().getPiece();
		return sourcePiece != null && sourcePiece.canMove(board, move.getStart(), move.getEnd());
	}
	
	/**
	 * Performs a castling move.
	 * 
	 * @param move {@link Move} Movement data
	 */
	private void makeRookCastlingMove(Move move) {
		Square rookSquare = move.getRookCastlingSquare();
		board.getSquare(rookSquare.getRow(), move.getCastlingDirection().getEndingRookCol()).setPiece(rookSquare.getPiece());
		move.getRookCastlingSquare().setPiece(null);
		move.setEnd(board.getSquare(move.getEnd().getRow(), move.getCastlingDirection().getEndingKingCol()));
	}
	
	/**
	 * Updates the necessary data when moving.
	 * 
	 * @param move {@link Move} Movement data
	 */
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
	
	/**
	 * Updates the status of the game.
	 */
	private void updateGameStatus() {		
		this.setStatus(GameStatus.get(board, getOpponent()));
	}
	
	/**
	 * Updates the next player's turn.
	 */
	private void updatePlayerCurrentTurn() {
		this.currentTurn = getOpponent();
	}
	
	/**
	 * Gets the opponent of the player who has the current turn.
	 * 
	 * @return {@link Player}
	 */
	private Player getOpponent() {
		return this.currentTurn == players[0] ? players[1] : players[0];
	}
	
	/**
	 * Gets the player of the colour passed by parameter.
	 * 
	 * @param colour {@link Colour} Player colour
	 * @return {@link Player}
	 */
	public Player getPlayer(Colour colour) {
		return colour.isWhite() ? players[0] : players[1];
	}
	
	/**
	 * Gets the last move made in the game.
	 * 
	 * @return {@link Move}
	 */
	public Move getLastMove() {
		return this.movesPlayed.get(this.movesPlayed.size() - 1);
	}
}
