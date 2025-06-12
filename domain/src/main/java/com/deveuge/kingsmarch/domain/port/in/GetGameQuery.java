package com.deveuge.kingsmarch.domain.port.in;

import com.deveuge.kingsmarch.domain.engine.Game;
import com.deveuge.kingsmarch.domain.model.GameId;

public interface GetGameQuery {
    Game get(GameId gameId);
}
