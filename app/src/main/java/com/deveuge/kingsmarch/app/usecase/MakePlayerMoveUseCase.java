package com.deveuge.kingsmarch.app.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.deveuge.kingsmarch.domain.engine.Game;
import com.deveuge.kingsmarch.domain.engine.Move;
import com.deveuge.kingsmarch.domain.engine.Player;
import com.deveuge.kingsmarch.domain.engine.Position;
import com.deveuge.kingsmarch.domain.model.Colour;
import com.deveuge.kingsmarch.infra.messaging.MoveResponse;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MakePlayerMoveUseCase {

	@Autowired
	@Qualifier("singleplayerGame")
	private Game game;

	public MoveResponse makeMove(String source, String target) {
		Player player = game.getPlayer(Colour.WHITE);
		boolean moveCorrect = game.move(player, new Position(source), new Position(target));
		MoveResponse response = new MoveResponse(moveCorrect);

		if (moveCorrect) {
			Move move = game.getLastMove();
			response.setMoveData(game, move);
		}

		return response;
	}
}