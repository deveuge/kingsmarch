package com.deveuge.kingsmarch.app.usecase;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.deveuge.kingsmarch.domain.engine.Board;
import com.deveuge.kingsmarch.domain.engine.Game;
import com.deveuge.kingsmarch.domain.port.out.GameSessionPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StartSingleplayerGameUseCase {

	private final GameSessionPort gameSessionPort;

	public Game startNewGame(Optional<String> fen) {
		Game game = gameSessionPort.getCurrentGame();
		if (fen.isPresent()) {
			game.setBoard(new Board(fen.get()));
		} else {
			game = new Game();
		}
		return game;
	}
}