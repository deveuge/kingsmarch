package com.deveuge.kingsmarch.domain.port.out;

import com.deveuge.kingsmarch.domain.engine.Game;

public interface GameSessionPort {
    Game getCurrentGame();
    void setCurrentGame(Game game);
    void deleteCurrentGame();
}