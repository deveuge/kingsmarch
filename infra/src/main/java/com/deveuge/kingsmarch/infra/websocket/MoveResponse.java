package com.deveuge.kingsmarch.infra.websocket;

import com.deveuge.kingsmarch.domain.engine.Game;
import com.deveuge.kingsmarch.domain.engine.Move;
import com.deveuge.kingsmarch.domain.engine.types.GameStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MoveResponse {

	private ResponseType responseType;
	private String gameFEN;
	private boolean refresh;
	private boolean capture;
	private boolean promotion;
	private boolean endOfGame;
	private GameStatus gameStatus;
	private String move;
	
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
