package com.deveuge.kingsmarch.engine;

import com.deveuge.kingsmarch.engine.pieces.Piece;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Move {
	
	private Player player;
	private Square start;
	private Square end;
	private Piece pieceMoved;
	private Piece pieceKilled;
	private boolean castlingMove = false;

	public Move(Player player, Square start, Square end) {
		this.player = player;
		this.start = start;
		this.end = end;
		this.pieceMoved = start.getPiece();
		this.pieceKilled = end.getPiece();
	}

	public boolean isCastlingMove() {
		return this.castlingMove;
	}

	public void setCastlingMove(boolean castlingMove) {
		this.castlingMove = castlingMove;
	}
}
