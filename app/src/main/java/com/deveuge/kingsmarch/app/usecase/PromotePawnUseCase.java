package com.deveuge.kingsmarch.app.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.deveuge.kingsmarch.domain.engine.Game;
import com.deveuge.kingsmarch.domain.engine.Move;
import com.deveuge.kingsmarch.domain.engine.Player;
import com.deveuge.kingsmarch.domain.engine.piece.Piece;
import com.deveuge.kingsmarch.domain.model.Colour;
import com.deveuge.kingsmarch.infra.messaging.MoveResponse;

@Service
public class PromotePawnUseCase {

	@Autowired
	@Qualifier("singleplayerGame")
	private Game game;

	public MoveResponse promote(String promotion) {
		Player player = game.getPlayer(Colour.WHITE);
		Move move = game.getLastMove();

		if (!move.isPawnPromotion() || !player.getColour().equals(move.getPieceMoved().getColour())) {
			return new MoveResponse(false);
		}

		Piece piece = Piece.createPromotionPiece(promotion, player.getColour());
		move.getEnd().setPiece(piece);

		MoveResponse response = new MoveResponse(true);
		response.setMoveData(game, move);
		response.setRefresh(true);
		return response;
	}
}