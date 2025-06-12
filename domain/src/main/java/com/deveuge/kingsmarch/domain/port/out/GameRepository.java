package com.deveuge.kingsmarch.domain.port.out;

import com.deveuge.kingsmarch.domain.engine.Game;
import com.deveuge.kingsmarch.domain.model.GameId;

public interface GameRepository {

	Game get(GameId id);
	void add(GameId id, Game game);
	void remove(GameId id);
}
