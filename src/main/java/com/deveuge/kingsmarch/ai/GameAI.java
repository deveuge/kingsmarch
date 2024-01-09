package com.deveuge.kingsmarch.ai;

import java.util.ArrayList;
import java.util.List;

import com.deveuge.kingsmarch.engine.Board;
import com.deveuge.kingsmarch.engine.Game;
import com.deveuge.kingsmarch.engine.Move;
import com.deveuge.kingsmarch.engine.Square;
import com.deveuge.kingsmarch.engine.pieces.Piece;
import com.deveuge.kingsmarch.engine.types.Colour;
import com.deveuge.kingsmarch.engine.types.GameStatus;
import com.deveuge.kingsmarch.engine.util.GameHelper;

public class GameAI {
	
	public static final Colour AI_COLOUR = Colour.BLACK;
	
	/**
	 * Gets the next move.
	 * 
	 * @param board {@link Game} Current game
	 * @return {@link Move} Next move
	 */
	public static Move getNextMove(Game game) {
        return minimaxRoot(game, 2, -10000, 10000, false);
	}
	
	/**
	 * Minimax algorithm root method.
	 * 
	 * @param game	       {@link Game} Current game situation
	 * @param depth        int Depth at which to search in the recursive tree
	 * @param alpha        int Minimum score
	 * @param beta         int Maximum score
	 * @param isMaximising boolean Whether the player is to maximise or minimise
	 * @return {@link Move} Next move
	 */
	private static Move minimaxRoot(Game game, int depth, int alpha, int beta, boolean isMaximising) {
		Board board = game.getBoard();
		List<Move> possibleMovements = getPossibleMovements(board);
		List<Move> openingMovements = OpeningBook.getNext(game.getMovesPlayed(AI_COLOUR), possibleMovements);
		if(openingMovements != null) {
			possibleMovements = openingMovements;
		}
		
		double bestValue = Integer.MIN_VALUE;
		Move bestMove = null;
		
		for (Move move : possibleMovements) {
			Board temporalBoard = GameHelper.makeTemporalMove(board, move.getStart(), move.getEnd(), move.getPieceMoved());
			List<Move> temporalMovesPlayed = new ArrayList<>(game.getMovesPlayed(AI_COLOUR));
			temporalMovesPlayed.add(move);
			double value = minimax(temporalBoard, temporalMovesPlayed, depth - 1, alpha, beta, isMaximising);
			if (value > bestValue) {
				bestValue = value;
				bestMove = move;
			}
		}
		return bestMove;
	}
	
	/**
	 * Minimax algorithm recursive method.
	 * 
	 * @param board        {@link Board} Current board situation
	 * @param historic     {@link List}<{@link Move}> List of movements made by the player
	 * @param depth        int Depth at which to search in the recursive tree
	 * @param alpha        int Minimum score
	 * @param beta         int Maximum score
	 * @param isMaximising boolean Whether the player is to maximise or minimise
	 * @return int Best value
	 */
	private static int minimax(Board board, List<Move> historic, int depth, int alpha, int beta, boolean isMaximising) {
		if(depth == 0 || GameStatus.get(board, AI_COLOUR.getOpposite()).isEndOfGame()) {
			return -evaluateBoard(board);
		}
		
		List<Move> possibleMovements = getPossibleMovements(board);
		List<Move> openingMovements = OpeningBook.getNext(historic, possibleMovements);
		if(openingMovements != null) {
			possibleMovements = openingMovements;
		}
		
		if(isMaximising) {
			int bestValue = Integer.MIN_VALUE;
			for(Move move : possibleMovements) {
				int currentValue = calculateMinimaxValue(board, historic, move, depth, alpha, beta, false);
				if(currentValue > bestValue) {
					bestValue = currentValue;
				}
				alpha = Math.max(alpha, currentValue);
				if(beta <= alpha) {
					break;
				}
			}
			return bestValue;
		} else {
			int bestValue = Integer.MAX_VALUE;
			for(Move move : possibleMovements) {
				int currentValue = calculateMinimaxValue(board, historic, move, depth, alpha, beta, true);
				if(currentValue < bestValue) {
					bestValue = currentValue;
				}
				beta = Math.min(beta, currentValue);
				if(beta <= alpha) {
					break;
				}
			}
			return bestValue;
		}
	}
	
	/**
	 * Gets the list of legal moves the player can make in the current board state.
	 * 
	 * @param board {@link Board} Current board situation
	 * @return {@link}List<{@link Move}> List of legal moves
	 */
	private static List<Move> getPossibleMovements(Board board) {
		List<Move> possibleMovements = new ArrayList<>();
		
		for(Square square : board.getOccupiedSquares(AI_COLOUR)) {
    		Piece piece = square.getPiece();
    		List<Square> potentialSquares = piece.getPotentialMoves(board, square);
    		for(Square potentialSquare : potentialSquares) {
    			if(piece.canMove(board, square, potentialSquare)) {
    				possibleMovements.add(new Move(square, potentialSquare, piece));
    			}
    		}
		}
		return possibleMovements;
	}
	
	/**
	 * Calculates the minimax value.
	 * 
	 * @param board        {@link Board} Current board situation
	 * @param depth        int Depth at which to search in the recursive tree
	 * @param alpha        int Minimum score
	 * @param beta         int Maximum score
	 * @param isMaximising boolean Whether the player is to maximise or minimise
	 * @return int calculated value
	 */
	private static int calculateMinimaxValue(Board board, List<Move> historic, Move move, int depth, int alpha, int beta, boolean isMaximising) {
		Board temporalBoard = GameHelper.makeTemporalMove(board, move.getStart(), move.getEnd(), move.getPieceMoved());
		return minimax(temporalBoard, historic, depth - 1, alpha, beta, isMaximising);
	}

	/**
	 * Calculates the current board value.
	 * 
	 * @param board {@link Board} Current board situation
	 * @return int board value
	 */
	private static int evaluateBoard(Board board) {
	    int totalEvaluation = 0;
	    Square[][] squares = board.getSquares();
	    for(Square[] row : squares) {
	    	for(Square square : row) {
	    		if(square.getPiece() != null) {
	    			totalEvaluation += square.getPiece().getBoardValue(square.getRow(), square.getCol());
	    		}
	    	}
	    }
	    return totalEvaluation;
	}

}
