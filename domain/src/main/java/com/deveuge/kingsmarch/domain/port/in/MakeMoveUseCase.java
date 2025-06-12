package com.deveuge.kingsmarch.domain.port.in;

import com.deveuge.kingsmarch.domain.engine.Move;
import com.deveuge.kingsmarch.domain.engine.Position;
import com.deveuge.kingsmarch.domain.model.GameId;

public interface MakeMoveUseCase {
	Move makeMove(GameId gameId, Position from, Position to);
}