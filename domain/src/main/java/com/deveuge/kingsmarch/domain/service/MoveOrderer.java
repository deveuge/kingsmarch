package com.deveuge.kingsmarch.domain.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.deveuge.kingsmarch.domain.engine.Board;
import com.deveuge.kingsmarch.domain.engine.Move;
import com.deveuge.kingsmarch.domain.engine.piece.Bishop;
import com.deveuge.kingsmarch.domain.engine.piece.Knight;
import com.deveuge.kingsmarch.domain.engine.piece.Piece;

/**
 * Move ordering implementation to improve alpha-beta pruning efficiency. 
 * Better moves are searched first, leading to more cutoffs.
 */
public class MoveOrderer {

	// Killer moves storage (moves that caused beta cutoffs at each depth)
	private final Move[][] killerMoves = new Move[64][2]; // Max depth 64, 2 killers per depth

	// History heuristic (moves that historically performed well)
	private final int[][][][] historyTable = new int[8][8][8][8]; // from_row, from_col, to_row, to_col

	/**
	 * Orders moves to improve search efficiency. Priority order: 
	 * - 1. Hash/PV move (from transposition table) 
	 * - 2. Captures (ordered by MVV-LVA) 
	 * - 3. Killer moves
	 * - 4. History heuristic 
	 * - 5. Other moves
	 */
	public List<Move> orderMoves(Board board, List<Move> moves, Move hashMove) {
		List<ScoredMove> scoredMoves = new ArrayList<>();

		for (Move move : moves) {
			int score = scoreMove(board, move, hashMove, 0); // depth 0 for now
			scoredMoves.add(new ScoredMove(move, score));
		}

		// Sort by score (highest first)
		scoredMoves.sort(Comparator.comparingInt(ScoredMove::getScore).reversed());

		List<Move> orderedMoves = new ArrayList<>();
		for (ScoredMove scoredMove : scoredMoves) {
			orderedMoves.add(scoredMove.getMove());
		}

		return orderedMoves;
	}

	/**
	 * Scores a move for ordering purposes.
	 */
	private int scoreMove(Board board, Move move, Move hashMove, int depth) {
		int score = 0;

		// 1. Hash move gets highest priority
		if (hashMove != null && movesEqual(move, hashMove)) {
			return 1000000;
		}

		// 2. Captures - use MVV-LVA (Most Valuable Victim - Least Valuable Attacker)
		if (isCapture(move)) {
			int victimValue = move.getEnd().getPiece().getValue();
			int attackerValue = move.getPieceMoved().getValue();
			score = 100000 + (victimValue * 100 - attackerValue);
		}

		// 3. Promotions
		if (move.isPawnPromotion()) {
			score += 90000;
		}

		// 4. Castling
		if (move.isCastlingMove()) {
			score += 50000;
		}

		// 5. Killer moves
		if (isKillerMove(move, depth)) {
			score += 80000;
		}

		// 6. History heuristic
		score += getHistoryScore(move);

		// 7. Positional bonuses
		score += getPositionalScore(board, move);

		return score;
	}

	/**
	 * Checks if a move is a capture.
	 */
	private boolean isCapture(Move move) {
		return move.getEnd().getPiece() != null || move.isEnPassant();
	}

	/**
	 * Checks if a move is a killer move.
	 */
	private boolean isKillerMove(Move move, int depth) {
		if (depth >= killerMoves.length)
			return false;

		Move[] killers = killerMoves[depth];
		return (killers[0] != null && movesEqual(move, killers[0]))
				|| (killers[1] != null && movesEqual(move, killers[1]));
	}

	/**
	 * Gets the history score for a move.
	 */
	private int getHistoryScore(Move move) {
		int fromRow = move.getStart().getRow();
		int fromCol = move.getStart().getCol();
		int toRow = move.getEnd().getRow();
		int toCol = move.getEnd().getCol();

		return historyTable[fromRow][fromCol][toRow][toCol];
	}

	/**
	 * Gets a basic positional score for the move.
	 */
	private int getPositionalScore(Board board, Move move) {
		int score = 0;

		// Center control bonus
		int toRow = move.getEnd().getRow();
		int toCol = move.getEnd().getCol();

		// Center squares (d4, d5, e4, e5)
		if ((toRow >= 3 && toRow <= 4) && (toCol >= 3 && toCol <= 4)) {
			score += 10;
		}

		// Extended center
		if ((toRow >= 2 && toRow <= 5) && (toCol >= 2 && toCol <= 5)) {
			score += 5;
		}

		// Development bonus for pieces
		Piece piece = move.getPieceMoved();
		if (piece instanceof Knight || piece instanceof Bishop) {
			if (piece.isFirstMove()) {
				score += 15; // Development bonus
			}
		}

		return score;
	}

	/**
	 * Records a killer move that caused a beta cutoff.
	 */
	public void recordKiller(Move move, int depth) {
		if (depth >= killerMoves.length || isCapture(move)) {
			return; // Don't store captures as killers
		}

		Move[] killers = killerMoves[depth];

		// Don't store duplicate killers
		if (killers[0] != null && movesEqual(move, killers[0])) {
			return;
		}

		// Shift killers: new move becomes first killer, first becomes second
		killers[1] = killers[0];
		killers[0] = move;
	}

	/**
	 * Records a move in the history table for future ordering.
	 */
	public void recordHistory(Move move, int depth) {
		if (isCapture(move)) {
			return; // Don't record captures in history
		}

		int fromRow = move.getStart().getRow();
		int fromCol = move.getStart().getCol();
		int toRow = move.getEnd().getRow();
		int toCol = move.getEnd().getCol();

		// Increase history score based on depth (deeper searches are more valuable)
		historyTable[fromRow][fromCol][toRow][toCol] += depth * depth;

		// Prevent overflow
		if (historyTable[fromRow][fromCol][toRow][toCol] > 10000) {
			ageHistoryTable();
		}
	}

	/**
	 * Ages the history table to prevent old entries from dominating.
	 */
	private void ageHistoryTable() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				for (int k = 0; k < 8; k++) {
					for (int l = 0; l < 8; l++) {
						historyTable[i][j][k][l] /= 2;
					}
				}
			}
		}
	}

	/**
	 * Clears the killer moves and history table.
	 */
	public void clear() {
		for (int i = 0; i < killerMoves.length; i++) {
			killerMoves[i][0] = null;
			killerMoves[i][1] = null;
		}

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				for (int k = 0; k < 8; k++) {
					for (int l = 0; l < 8; l++) {
						historyTable[i][j][k][l] = 0;
					}
				}
			}
		}
	}

	/**
	 * Compares two moves for equality.
	 */
	private boolean movesEqual(Move move1, Move move2) {
		return move1.getStart().getRow() == move2.getStart().getRow()
				&& move1.getStart().getCol() == move2.getStart().getCol()
				&& move1.getEnd().getRow() == move2.getEnd().getRow()
				&& move1.getEnd().getCol() == move2.getEnd().getCol();
	}

	/**
	 * Helper class to store moves with their scores.
	 */
	private static class ScoredMove {
		private final Move move;
		private final int score;

		public ScoredMove(Move move, int score) {
			this.move = move;
			this.score = score;
		}

		public Move getMove() {
			return move;
		}

		public int getScore() {
			return score;
		}
	}
}