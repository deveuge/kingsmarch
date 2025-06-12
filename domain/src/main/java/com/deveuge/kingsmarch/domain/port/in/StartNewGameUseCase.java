package com.deveuge.kingsmarch.domain.port.in;

import java.util.Optional;

import com.deveuge.kingsmarch.domain.model.GameId;

public interface StartNewGameUseCase {
	GameId startNewGame(Optional<GameId> id, Optional<String> fen);
}
