package com.deveuge.kingsmarch.engine.types;

import java.util.List;

import com.deveuge.kingsmarch.engine.Board;
import com.deveuge.kingsmarch.engine.Player;
import com.deveuge.kingsmarch.engine.Square;
import com.deveuge.kingsmarch.engine.pieces.King;
import com.deveuge.kingsmarch.engine.pieces.Piece;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GameStatus {
	ACTIVE(false), 
    BLACK_WIN(true), 
    WHITE_WIN(true), 
    STALEMATE(true);
    
    boolean endOfGame;
    
    public static GameStatus get(Board board, Player opponentPlayer) {
    	Square opponentKingSquare = board.getKingSquare(opponentPlayer.getColour());
		King opponentKing = (King) opponentKingSquare.getPiece();
		if(!opponentKing.isInCheck(board, opponentKingSquare)) {
			return isStalemate(board, opponentPlayer) ? STALEMATE : ACTIVE;
		}
		
		if(isCheckmate(board, opponentPlayer)) {
			return opponentPlayer.isWhiteSide() ? BLACK_WIN : WHITE_WIN;
		}
		
		return ACTIVE;
    }

	/**
	 * Checks if the player whose turn it is to move has no legal move.
	 * 
	 * @param board          {@link Board} Current board situation
	 * @param opponentPlayer {@link Player} Opponent player
	 * @return true if it is a stalemate, false otherwise
	 */
    private static boolean isStalemate(Board board, Player opponentPlayer) {
    	for(Square square : board.getOccupiedSquares(opponentPlayer.getColour())) {
    		Piece opponentPiece = square.getPiece();
    		List<Square> potentialSquares = opponentPiece.getPotentialMoves(board, square);
    		for(Square potentialSquare : potentialSquares) {
    			if(opponentPiece.canMove(board, square, potentialSquare)) {
    				return false;
    			}
    		}
    	}
		return true;
	}
    
    private static boolean isCheckmate(Board board, Player opponentPlayer) {
		// TODO Auto-generated method stub
		return false;
	}
}
