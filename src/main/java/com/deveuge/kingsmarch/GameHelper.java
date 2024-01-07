package com.deveuge.kingsmarch;

import java.util.HashMap;
import java.util.Map;

import com.deveuge.kingsmarch.engine.Board;
import com.deveuge.kingsmarch.engine.Game;
import com.deveuge.kingsmarch.engine.Square;
import com.deveuge.kingsmarch.engine.pieces.Piece;

public class GameHelper {

	private static Map<String, Game> games = new HashMap<>();

	public static Game get(String id) {
		return games.get(id);
	}

	public static void addGame(String id, Game game) {
		if(games.get(id) == null) {
			games.put(id, game);
		}
	}
	
	public static void removeGame(String id) {
		games.remove(id);
	}
	
	/**
	 * Performs a temporal move without affecting the current state of the board.
	 * 
	 * @param board {@link Board} Current board situation
	 * @param start {@link Square} Starting position of the movement
	 * @param end   {@link Square} Final position of the movement
	 * @param piece {@link Piece} Piece to be moved
	 * @return {@Board} Deep copy of the board having executed the move
	 */
	public static Board makeTemporalMove(Board board, Square start, Square end, Piece piece) {
		Board temporalBoard = new Board(board);
		temporalBoard.getSquare(end.getRow(), end.getCol()).setPiece(piece);
		temporalBoard.getSquare(start.getRow(), start.getCol()).setPiece(null);
		return temporalBoard;
	}
}
