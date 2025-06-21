package com.deveuge.kingsmarch.infra.adapter.out;

import org.springframework.stereotype.Component;

import com.deveuge.kingsmarch.domain.engine.Game;
import com.deveuge.kingsmarch.domain.port.out.GameSessionPort;

import jakarta.servlet.http.HttpSession;

@Component
public class HttpSessionGameAdapter implements GameSessionPort {

	private static final String SESSION_KEY = "singleplayerGame";

	private final HttpSession session;

	public HttpSessionGameAdapter(HttpSession session) {
		this.session = session;
	}

	@Override
	public Game getCurrentGame() {
		Game game = (Game) session.getAttribute(SESSION_KEY);
		if (game == null) {
			game = new Game();
			session.setAttribute(SESSION_KEY, game);
		}
		return game;
	}

	@Override
	public void setCurrentGame(Game game) {
		session.setAttribute(SESSION_KEY, game);
	}

	@Override
	public void deleteCurrentGame() {
		session.removeAttribute(SESSION_KEY);
	}
}