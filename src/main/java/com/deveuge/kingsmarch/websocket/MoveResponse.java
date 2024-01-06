package com.deveuge.kingsmarch.websocket;

import com.deveuge.kingsmarch.engine.Game;
import com.deveuge.kingsmarch.engine.Move;
import com.deveuge.kingsmarch.engine.types.GameStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MoveResponse {

	private ResponseType responseType;
	private String gameFEN;
	private boolean refresh;
	private boolean capture;
	private boolean promotion;
	private boolean endOfGame;
	private GameStatus gameStatus;
	
	public MoveResponse(boolean correct) {
		super();
		this.responseType = correct ? ResponseType.OK : ResponseType.SNAPBACK;
	}

	public void setMoveData(Game game, Move move) {
		this.refresh = move.isCastlingMove() || move.isEnPassant();
		this.capture = move.getPieceKilled() != null;
		this.promotion = move.isPawnPromotion();
		this.gameFEN = game.getBoard().getFEN();
		this.gameStatus = game.getStatus();
		this.endOfGame = game.getStatus().isEndOfGame();
	}
	
	
	
}
