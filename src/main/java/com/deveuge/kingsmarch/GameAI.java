package com.deveuge.kingsmarch;

import java.util.ArrayList;
import java.util.List;

import com.deveuge.kingsmarch.engine.Board;
import com.deveuge.kingsmarch.engine.Game;
import com.deveuge.kingsmarch.engine.Move;
import com.deveuge.kingsmarch.engine.Square;
import com.deveuge.kingsmarch.engine.pieces.Piece;
import com.deveuge.kingsmarch.engine.types.Colour;

public class GameAI {
	
	public static Move getBestMove(Game game) {
        return minimaxRoot(game.getBoard(), 2, -10000, 10000, false);
	}
	
	private static Move minimaxRoot(Board board, int depth, int alpha, int beta, boolean isMaximising) {
		List<Move> possibleMovements = getPossibleMovements(board);
		double bestValue = Integer.MIN_VALUE;
		Move bestMove = null;
		
		for (Move move : possibleMovements) {
			Board temporalBoard = makeTemporalMove(board, move);
			double value = minimax(temporalBoard, depth - 1, alpha, beta, isMaximising);
			if (value > bestValue) {
				bestValue = value;
				bestMove = move;
			}
		}
		return bestMove;
	}
	
	private static int minimax(Board board, int depth, int alpha, int beta, boolean isMaximisingPlayer) {
		if(depth == 0) {
			return -evaluateBoard(board);
		}
		
		List<Move> possibleMovements = getPossibleMovements(board);
		
		if(isMaximisingPlayer) {
			int bestValue = Integer.MIN_VALUE;
			for(Move move : possibleMovements) {
				int currentValue = calculateMinimaxValue(board, move, depth, alpha, beta, false);
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
				int currentValue = calculateMinimaxValue(board, move, depth, alpha, beta, true);
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
	
	private static List<Move> getPossibleMovements(Board board) {
		List<Move> possibleMovements = new ArrayList<>();
		
		for(Square square : board.getOccupiedSquares(Colour.BLACK)) {
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
	
	private static int calculateMinimaxValue(Board board, Move move, int depth, int alpha, int beta, boolean isMaximisingPlayer) {
		Board temporalBoard = makeTemporalMove(board, move);
		return minimax(temporalBoard, depth - 1, alpha, beta, isMaximisingPlayer);
	}
	
	private static Board makeTemporalMove(Board board, Move move) {
		Square start = move.getStart();
		Square end = move.getEnd();
		Board temporalBoard = new Board(board);
		temporalBoard.getSquare(start.getRow(), start.getCol()).setPiece(null);
		temporalBoard.getSquare(end.getRow(), end.getCol()).setPiece(move.getPieceMoved());
		return temporalBoard;
	}

	private static int evaluateBoard(Board board) {
	    var totalEvaluation = 0;
	    Square[][] squares = board.getSquares();
	    for(Square[] row : squares) {
	    	for(Square square : row) {
	    		if(square.getPiece() != null) {
	    			totalEvaluation += square.getPiece().getValue();
	    		}
	    	}
	    }
	    return totalEvaluation;
	}

}
