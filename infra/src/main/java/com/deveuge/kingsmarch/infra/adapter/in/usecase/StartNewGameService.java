package com.deveuge.kingsmarch.infra.adapter.in.usecase;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.deveuge.kingsmarch.domain.engine.Board;
import com.deveuge.kingsmarch.domain.engine.Game;
import com.deveuge.kingsmarch.domain.model.GameId;
import com.deveuge.kingsmarch.domain.port.in.StartNewGameUseCase;
import com.deveuge.kingsmarch.domain.port.out.GameRepository;

@Service
public class StartNewGameService implements StartNewGameUseCase {

    private final GameRepository gameRepository;

    public StartNewGameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public GameId startNewGame(Optional<GameId> id, Optional<String> fen) {
    	GameId gameId = id.orElse(GameId.generate());
        gameRepository.add(gameId, new Game());
        if(fen.isPresent()) {
        	Game game = gameRepository.get(gameId);
        	game.setBoard(new Board(fen.get()));
        }
        return gameId;
    }
}