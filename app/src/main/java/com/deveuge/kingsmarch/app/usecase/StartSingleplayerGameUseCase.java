package com.deveuge.kingsmarch.app.usecase;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.deveuge.kingsmarch.domain.engine.Board;
import com.deveuge.kingsmarch.domain.engine.Game;

@Service
public class StartSingleplayerGameUseCase {

	@Autowired
	@Qualifier("singleplayerGame")
	private Game game;

	public void startNewGame(Optional<String> fen) {
		if (fen.isPresent()) {
			game.setBoard(new Board(fen.get()));
		} else {
			game = new Game();
		}
	}

	public Game getGame() {
		return game;
	}
}