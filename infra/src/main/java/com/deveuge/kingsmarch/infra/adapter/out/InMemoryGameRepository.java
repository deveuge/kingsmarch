package com.deveuge.kingsmarch.infra.adapter.out;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.deveuge.kingsmarch.domain.engine.Game;
import com.deveuge.kingsmarch.domain.model.GameId;
import com.deveuge.kingsmarch.domain.port.out.GameRepository;

@Component
public class InMemoryGameRepository implements GameRepository {

	private static Map<GameId, Game> games = new HashMap<>();

	@Override
	public Game get(GameId id) {
		return games.get(id);
	}

	@Override
	public void add(GameId id, Game game) {
		if(games.get(id) == null) {
			games.put(id, game);
		}
	}
	
	@Override
	public void remove(GameId id) {
		games.remove(id);
	}
	
}