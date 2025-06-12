package com.deveuge.kingsmarch.app.strategy;

import org.springframework.stereotype.Service;

import com.deveuge.kingsmarch.domain.engine.Game;
import com.deveuge.kingsmarch.domain.engine.Move;
import com.deveuge.kingsmarch.domain.service.GameAI;
import com.deveuge.kingsmarch.infra.adapter.out.FileBasedOpeningBookFactory;

@Service
public class GameAIStrategy {

    private final GameAI gameAI;

    public GameAIStrategy(FileBasedOpeningBookFactory factory) {
        this.gameAI = new GameAI(factory.getOpeningBook());
    }

    public Move computeMove(Game game) {
        return gameAI.getNextMove(game);
    }
}