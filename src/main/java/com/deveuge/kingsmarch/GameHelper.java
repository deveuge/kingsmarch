package com.deveuge.kingsmarch;

import java.util.HashMap;
import java.util.Map;

import com.deveuge.kingsmarch.engine.Game;

public class GameHelper {

	private static Map<String, Game> games = new HashMap<>();

	public static Game get(String id) {
		return games.get(id);
	}

	public static void addGame(String id, Game game) {
		if(games.get(id) == null) {
			games.put(id, game);
		}
	}
	
	public static void removeGame(String id) {
		games.remove(id);
	}
}
