package com.deveuge.kingsmarch.domain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.deveuge.kingsmarch.domain.engine.Board;
import com.deveuge.kingsmarch.domain.engine.Game;
import com.deveuge.kingsmarch.domain.engine.Move;
import com.deveuge.kingsmarch.domain.engine.Square;
import com.deveuge.kingsmarch.domain.engine.piece.Piece;
import com.deveuge.kingsmarch.domain.model.Colour;
import com.deveuge.kingsmarch.domain.model.Difficulty;
import com.deveuge.kingsmarch.domain.model.GameStatus;
import com.deveuge.kingsmarch.domain.model.opening.OpeningBook;

public class GameAI {
	
	public static final Colour AI_COLOUR = Colour.BLACK;
	private final OpeningBook openingBook;
	private final TranspositionTable transpositionTable;
	private final MoveOrderer moveOrderer;
	private final BoardEvaluator boardEvaluator;
	
	private long nodesEvaluated = 0;
	private long totalPrunings = 0;

    public GameAI(OpeningBook openingBook) {
        this.openingBook = openingBook;
        this.transpositionTable = new TranspositionTable(1_000_000); // 1M entries
        this.moveOrderer = new MoveOrderer();
        this.boardEvaluator = new BoardEvaluator();
    }
    
    public AIStats getLastSearchStats() {
    	return new AIStats(nodesEvaluated, totalPrunings, transpositionTable.getHitRate());
    }
	
	/**
	 * Gets the next move using the configured difficulty.
	 */
	public Move getNextMove(Game game) {
		nodesEvaluated = 0;
		totalPrunings = 0;
		Difficulty currentDifficulty = game.getDifficulty();
		
		if (currentDifficulty.useTranspositionTable()) {
			transpositionTable.incrementAge(); // Age entries for better replacement
		}
		
        return minimaxRoot(game, currentDifficulty.getDepth(), Integer.MIN_VALUE, Integer.MAX_VALUE, true);
	}
	
	/**
	 * Enhanced minimax root with move ordering and better pruning.
	 */
	private Move minimaxRoot(Game game, int depth, int alpha, int beta, boolean isMaximizing) {
		Board board = game.getBoard();
		List<Move> possibleMovements = getPossibleMovements(board);
		
		// Check opening book first
		List<Move> openingMovements = openingBook.getNext(game.getMovesPlayed(AI_COLOUR), possibleMovements);
		if(openingMovements != null && !openingMovements.isEmpty()) {
			possibleMovements = openingMovements;
		} else {
			// Order moves for better alpha-beta pruning
			possibleMovements = moveOrderer.orderMoves(board, possibleMovements, null);
		}
		
		// Parallel evaluation at root level
		int threads = Math.min(2, Runtime.getRuntime().availableProcessors());
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		List<Future<MoveEvaluation>> futures = new ArrayList<>();

		for (Move move : possibleMovements) {
			futures.add(executor.submit(() -> {
				Board temporalBoard = board.makeTemporalMove(move.getStart(), move.getEnd(), move.getPieceMoved());
				List<Move> temporalMovesPlayed = new ArrayList<>(game.getMovesPlayed(AI_COLOUR));
				temporalMovesPlayed.add(move);
				int value = minimax(temporalBoard, temporalMovesPlayed, depth - 1, alpha, beta, false, move, game.getDifficulty());
				return new MoveEvaluation(move, value);
			}));
		}

		int bestValue = Integer.MIN_VALUE;
		Move bestMove = null;

		for (Future<MoveEvaluation> future : futures) {
			try {
				MoveEvaluation result = future.get();
				if (result.value > bestValue) {
					bestValue = result.value;
					bestMove = result.move;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		executor.shutdown();
		return bestMove;
	}
	
	/**
	 * Enhanced minimax with transposition table and better pruning.
	 * @param difficulty 
	 */
	private int minimax(Board board, List<Move> historic, int depth, int alpha, int beta, 
			boolean isMaximizing, Move lastMove, Difficulty currentDifficulty) {
		
		nodesEvaluated++;
		
		// Check transposition table first
		if (currentDifficulty.useTranspositionTable()) {
			TranspositionEntry entry = transpositionTable.lookup(board.getFEN());
			if (entry != null && entry.getDepth() >= depth) {
				switch (entry.getFlag()) {
					case EXACT:
						return entry.getValue();
					case LOWER_BOUND:
						alpha = Math.max(alpha, entry.getValue());
						break;
					case UPPER_BOUND:
						beta = Math.min(beta, entry.getValue());
						break;
				}
				if (alpha >= beta) {
					return entry.getValue();
				}
			}
		}
		
		// Terminal node check
		GameStatus status = GameStatus.get(board, isMaximizing ? AI_COLOUR : AI_COLOUR.getOpposite());
		if (depth == 0 || status.isEndOfGame()) {
			int evaluation = evaluatePosition(board, depth, status);
			
			// Store in transposition table
			if (currentDifficulty.useTranspositionTable()) {
				transpositionTable.store(board.getFEN(), evaluation, depth, TranspositionFlag.EXACT);
			}
			
			return evaluation;
		}
		
		List<Move> possibleMovements = getPossibleMovements(board, isMaximizing ? AI_COLOUR : AI_COLOUR.getOpposite());
		
		// Check opening book for AI moves
		if (isMaximizing) {
			List<Move> openingMovements = openingBook.getNext(historic, possibleMovements);
			if (openingMovements != null && !openingMovements.isEmpty()) {
				possibleMovements = openingMovements;
			}
		}
		
		// Order moves for better pruning
		possibleMovements = moveOrderer.orderMoves(board, possibleMovements, lastMove);
		
		int originalAlpha = alpha;
		int bestValue = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		
		for (Move move : possibleMovements) {
			Board temporalBoard = board.makeTemporalMove(move.getStart(), move.getEnd(), move.getPieceMoved());
			List<Move> newHistoric = isMaximizing ? addToHistoric(historic, move) : historic;
			
			int value = minimax(temporalBoard, newHistoric, depth - 1, alpha, beta, !isMaximizing, move, currentDifficulty);
			
			if (isMaximizing) {
				bestValue = Math.max(bestValue, value);
				alpha = Math.max(alpha, value);
			} else {
				bestValue = Math.min(bestValue, value);
				beta = Math.min(beta, value);
			}
			
			if (beta <= alpha) {
				totalPrunings++;
				break; // Alpha-beta cutoff
			}
		}
		
		// Store result in transposition table
		if (currentDifficulty.useTranspositionTable()) {
			TranspositionFlag flag;
			if (bestValue <= originalAlpha) {
				flag = TranspositionFlag.UPPER_BOUND;
			} else if (bestValue >= beta) {
				flag = TranspositionFlag.LOWER_BOUND;
			} else {
				flag = TranspositionFlag.EXACT;
			}
			transpositionTable.store(board.getFEN(), bestValue, depth, flag);
		}
		
		return bestValue;
	}
	
	/**
	 * Enhanced position evaluation with game phase awareness.
	 */
	private int evaluatePosition(Board board, int depth, GameStatus status) {
		// Handle terminal positions
		if (status.isEndOfGame()) {
			if (status == GameStatus.BLACK_WIN || status == GameStatus.WHITE_WIN) {
				return AI_COLOUR.equals(getWinningColor(status)) ? 10000 + depth : -10000 - depth;
			}
			return 0; // Draw
		}
		
		int totalEvaluation = 0;
		int materialBalance = 0;
		int pieceCount = 0;
		
		Square[][] squares = board.getSquares();
		for(Square[] row : squares) {
			for(Square square : row) {
				if(square.getPiece() != null) {
					Piece piece = square.getPiece();
					int pieceValue = piece.getBoardValue(square.getRow(), square.getCol());
					
					if (piece.getColour().equals(AI_COLOUR)) {
						totalEvaluation += pieceValue;
						materialBalance += Math.abs(pieceValue);
					} else {
						totalEvaluation -= pieceValue;
						materialBalance += Math.abs(pieceValue);
					}
					pieceCount++;
				}
			}
		}
		
		// Add positional bonuses based on game phase
		boolean isEndgame = pieceCount <= 12 || materialBalance < 2000;
		if (isEndgame) {
			totalEvaluation += boardEvaluator.evaluateEndgame(board, AI_COLOUR);
		} else {
			totalEvaluation += boardEvaluator.evaluateMiddlegame(board, AI_COLOUR);
		}
		
		return totalEvaluation;
	}
	
	private Colour getWinningColor(GameStatus status) {
	    return switch (status) {
	        case WHITE_WIN -> Colour.WHITE;
	        case BLACK_WIN -> Colour.BLACK;
	        default -> null;
	    };
	}
	
	private List<Move> addToHistoric(List<Move> historic, Move move) {
		List<Move> newHistoric = new ArrayList<>(historic);
		newHistoric.add(move);
		return newHistoric;
	}
	
	/**
	 * Gets possible movements for a specific color.
	 */
	private List<Move> getPossibleMovements(Board board, Colour colour) {
		List<Move> possibleMovements = new ArrayList<>();
		
		for(Square square : board.getOccupiedSquares(colour)) {
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
	 * Gets possible movements for AI color (backward compatibility).
	 */
	private List<Move> getPossibleMovements(Board board) {
		return getPossibleMovements(board, AI_COLOUR);
	}
	
	private static class MoveEvaluation {
	    final Move move;
	    final int value;

	    MoveEvaluation(Move move, int value) {
	        this.move = move;
	        this.value = value;
	    }
	}
	
	// Statistics class for debugging and tuning
	public static class AIStats {
		private final long nodesEvaluated;
		private final long totalPrunings;
		private final double transpositionHitRate;
		
		public AIStats(long nodesEvaluated, long totalPrunings, double transpositionHitRate) {
			this.nodesEvaluated = nodesEvaluated;
			this.totalPrunings = totalPrunings;
			this.transpositionHitRate = transpositionHitRate;
		}
		
		// Getters
		public long getNodesEvaluated() { return nodesEvaluated; }
		public long getTotalPrunings() { return totalPrunings; }
		public double getTranspositionHitRate() { return transpositionHitRate; }
		
		@Override
		public String toString() {
			return String.format("AI Stats - Nodes: %d, Prunings: %d, TT Hit Rate: %.2f%%", 
					nodesEvaluated, totalPrunings, transpositionHitRate * 100);
		}
	}
}
