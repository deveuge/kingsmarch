package com.deveuge.kingsmarch.infra.adapter.in.usecase;

import org.springframework.stereotype.Service;

import com.deveuge.kingsmarch.domain.engine.Game;
import com.deveuge.kingsmarch.domain.model.GameId;
import com.deveuge.kingsmarch.domain.port.in.GetGameQuery;
import com.deveuge.kingsmarch.domain.port.out.GameRepository;

@Service
public class GetGameService implements GetGameQuery {

    private final GameRepository gameRepository;

    public GetGameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public Game get(GameId gameId) {
        return gameRepository.get(gameId);
    }
}
