package com.deveuge.kingsmarch.domain.service;

import java.util.ArrayList;
import java.util.List;

import com.deveuge.kingsmarch.domain.engine.Board;
import com.deveuge.kingsmarch.domain.engine.Square;
import com.deveuge.kingsmarch.domain.engine.piece.King;
import com.deveuge.kingsmarch.domain.engine.piece.Pawn;
import com.deveuge.kingsmarch.domain.engine.piece.Piece;
import com.deveuge.kingsmarch.domain.model.Colour;

public class BoardEvaluator {

	/**
	 * Evaluates the board position during the middlegame phase from the perspective
	 * of the given colour. Combines material balance, center control, king safety,
	 * and piece mobility.
	 *
	 * @param board  The current game board.
	 * @param colour The colour of the player being evaluated.
	 * @return A positional score reflecting the quality of the middlegame position.
	 */
	public int evaluateMiddlegame(Board board, Colour colour) {
		int score = 0;
		score += evaluateMaterial(board, colour);
		score += evaluateCenterControl(board, colour);
		score += evaluateKingSafety(board, colour);
		score += evaluateMobility(board, colour);
		return score;
	}

	/**
	 * Evaluates the board position during the endgame phase from the perspective of
	 * the given colour. Focuses on material, king activity, and the presence of
	 * passed pawns.
	 *
	 * @param board  The current game board.
	 * @param colour The colour of the player being evaluated.
	 * @return A positional score reflecting the quality of the endgame position.
	 */
	public int evaluateEndgame(Board board, Colour colour) {
		int score = 0;
		score += evaluateMaterial(board, colour);
		score += evaluateKingActivity(board, colour);
		score += evaluatePassedPawns(board, colour);
		return score;
	}

	/**
	 * Evaluates the material balance on the board from the perspective of the given
	 * AI colour. Each piece has a predefined value, and the total score is the
	 * difference between AI and opponent material.
	 *
	 * @param board  The current game board.
	 * @param colour The colour of the AI player.
	 * @return A positive score if the AI is ahead in material, negative if behind.
	 */
	private int evaluateMaterial(Board board, Colour colour) {
		int score = 0;
		for (Square square : board.getOccupiedSquares()) {
			Piece piece = square.getPiece();
			int value = piece.getValue();
			score += piece.getColour() == colour ? value : -value;
		}
		return score;
	}

	/**
	 * Evaluates the safety of the AI's king based on check status and surrounding
	 * friendly pieces. Encourages castling and protective formations around the
	 * king.
	 *
	 * @param board  The current game board.
	 * @param colour The colour of the AI player.
	 * @return A safety score. Negative if in check, positive for protected
	 *         surroundings.
	 */
	private int evaluateKingSafety(Board board, Colour colour) {
		Square kingSquare = board.getKingSquare(colour);
		King king = (King) kingSquare.getPiece();

		if (king.isInCheck(board, kingSquare)) {
			return -50;
		}

		int nearbyAllies = 0;
		for (Square neighbor : getAdjacentSquares(kingSquare, board)) {
			Piece p = neighbor.getPiece();
			if (p != null && p.getColour() == colour) {
				nearbyAllies++;
			}
		}

		return 5 * nearbyAllies;
	}

	/**
	 * Evaluates the king's activity in the endgame phase. A king closer to the
	 * center is generally more active and rewarded accordingly.
	 *
	 * @param board  The current game board.
	 * @param colour The colour of the AI player.
	 * @return A score based on distance to the center. Closer = higher.
	 */
	private int evaluateKingActivity(Board board, Colour colour) {
		Square kingSquare = board.getKingSquare(colour);
		int row = kingSquare.getRow();
		int col = kingSquare.getCol();
		int centerDistance = Math.abs(3 - row) + Math.abs(3 - col);
		return -10 * centerDistance;
	}

	/**
	 * Returns all valid adjacent squares around the specified square. Used for king
	 * safety evaluation and proximity checks.
	 *
	 * @param square The square for which to retrieve adjacent squares.
	 * @param board  The game board.
	 * @return A list of adjacent squares.
	 */
	private List<Square> getAdjacentSquares(Square square, Board board) {
		List<Square> adjacent = new ArrayList<>();
		int row = square.getRow();
		int col = square.getCol();

		for (int dr = -1; dr <= 1; dr++) {
			for (int dc = -1; dc <= 1; dc++) {
				if (dr == 0 && dc == 0)
					continue;
				int newRow = row + dr;
				int newCol = col + dc;
				Square adjacentSquare = board.getSquare(newRow, newCol);
				if (adjacentSquare != null) {
					adjacent.add(adjacentSquare);
				}
			}
		}

		return adjacent;
	}

	/**
	 * Evaluates the control over central squares (d4, d5, e4, e5) by the AI's
	 * pieces. Encourages center dominance, especially in the opening and
	 * middlegame.
	 *
	 * @param board  The current game board.
	 * @param colour The colour of the AI player.
	 * @return A score based on number of central squares controlled.
	 */
	private int evaluateCenterControl(Board board, Colour colour) {
		int score = 0;
		List<Square> centerSquares = List.of(
				board.getSquare(3, 3), 
				board.getSquare(3, 4), 
				board.getSquare(4, 3),
				board.getSquare(4, 4));

		for (Square square : board.getOccupiedSquares(colour)) {
			Piece piece = square.getPiece();
			List<Square> targets = piece.getPotentialMoves(board, square);
			for (Square target : targets) {
				if (centerSquares.contains(target)) {
					score += 5;
				}
			}
		}

		return score;
	}

	/**
	 * Evaluates the mobility of the AI's pieces, i.e., the number of legal moves
	 * available. Higher mobility is typically associated with a better position.
	 *
	 * @param board  The current game board.
	 * @param colour The colour of the AI player.
	 * @return A mobility score based on available legal moves.
	 */
	private int evaluateMobility(Board board, Colour colour) {
		int mobility = 0;
		for (Square square : board.getOccupiedSquares(colour)) {
			Piece piece = square.getPiece();
			List<Square> moves = piece.getPotentialMoves(board, square);
			for (Square target : moves) {
				if (piece.canMove(board, square, target)) {
					mobility++;
				}
			}
		}
		return mobility * 5;
	}

	/**
	 * Evaluates the presence and advancement of passed pawns for the AI. Passed
	 * pawns are not blocked or opposed by enemy pawns on the same or adjacent
	 * files.
	 *
	 * @param board  The current game board.
	 * @param colour The colour of the AI player.
	 * @return A score favoring passed pawns closer to promotion.
	 */
	private int evaluatePassedPawns(Board board, Colour colour) {
		int score = 0;
		for (Square square : board.getOccupiedSquares(colour)) {
			Piece piece = square.getPiece();
			if (piece instanceof Pawn)
				continue;

			if (isPassedPawn(board, square, colour)) {
				int rank = colour.isWhite() ? square.getRow() : 7 - square.getRow();
				score += 30 + (rank * 10);
			}
		}
		return score;
	}

	/**
	 * Determines whether a pawn is a passed pawn (no opposing pawns ahead on
	 * same/adjacent files).
	 *
	 * @param board      The game board.
	 * @param pawnSquare The square containing the pawn.
	 * @param colour     The colour of the pawn.
	 * @return True if the pawn is passed, false otherwise.
	 */
	private boolean isPassedPawn(Board board, Square pawnSquare, Colour colour) {
		int direction = colour.isWhite() ? 1 : -1;
		int startRow = pawnSquare.getRow() + direction;
		int col = pawnSquare.getCol();

		for (int row = startRow; row >= 0 && row < 8; row += direction) {
			for (int offset = -1; offset <= 1; offset++) {
				int targetCol = col + offset;
				if (targetCol < 0 || targetCol > 7)
					continue;

				Square target = board.getSquare(row, targetCol);
				if (target != null && target.getPiece() != null && target.getPiece() instanceof Pawn
						&& target.getPiece().getColour() != colour) {
					return false;
				}
			}
		}
		return true;
	}

}
