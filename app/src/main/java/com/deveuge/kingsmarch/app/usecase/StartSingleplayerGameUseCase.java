package com.deveuge.kingsmarch.app.usecase;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.deveuge.kingsmarch.domain.engine.Board;
import com.deveuge.kingsmarch.domain.engine.Game;
import com.deveuge.kingsmarch.domain.model.Difficulty;
import com.deveuge.kingsmarch.domain.port.out.GameSessionPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StartSingleplayerGameUseCase {

	private final GameSessionPort gameSessionPort;

	public Game startNewGame(Difficulty difficulty, Optional<String> fen) {
		Game game = gameSessionPort.getCurrentGame();
		if (fen.isPresent()) {
			game = new Game();
			game.setBoard(new Board(fen.get()));
		}
		game.setDifficulty(difficulty);
		gameSessionPort.setCurrentGame(game);
		return game;
	}
	
	public Difficulty getDifficulty() {
		Game game = gameSessionPort.getCurrentGame();
		return game == null
				? Difficulty.getDefault()
				: game.getDifficulty();
	}
	
	public void restart() {
		gameSessionPort.deleteCurrentGame();
	}
}