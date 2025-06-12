package com.deveuge.kingsmarch.app.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.deveuge.kingsmarch.app.strategy.GameAIStrategy;
import com.deveuge.kingsmarch.domain.engine.Game;
import com.deveuge.kingsmarch.domain.engine.Move;
import com.deveuge.kingsmarch.domain.engine.Player;
import com.deveuge.kingsmarch.domain.engine.Position;
import com.deveuge.kingsmarch.domain.engine.piece.Pawn;
import com.deveuge.kingsmarch.domain.engine.piece.Piece;
import com.deveuge.kingsmarch.domain.service.GameAI;
import com.deveuge.kingsmarch.infra.messaging.MoveResponse;

@Service
public class MakeAIMoveUseCase {

	@Autowired
	@Qualifier("singleplayerGame")
	private Game game;

	private final GameAIStrategy gameAIStrategy;

	public MakeAIMoveUseCase(GameAIStrategy gameAIStrategy) {
		this.gameAIStrategy = gameAIStrategy;
	}

	public MoveResponse makeMove() {
		Player player = game.getPlayer(GameAI.AI_COLOUR);
		Move bestMove = gameAIStrategy.computeMove(game);

		Position start = new Position(bestMove.getStart());
		Position end = new Position(bestMove.getEnd());
		boolean isPawnPromotion = bestMove.getPieceMoved() instanceof Pawn && end.getRow() == 0;

		game.move(player, start, end);

		if (isPawnPromotion) {
			Move move = game.getLastMove();
			Piece piece = Piece.createPromotionPiece("q", player.getColour());
			move.getEnd().setPiece(piece);
		}

		MoveResponse response = new MoveResponse(true);
		response.setMoveData(game, bestMove);
		response.setMove(bestMove.getAlgebraicNotation());
		response.setRefresh(isPawnPromotion || response.isRefresh());
		return response;
	}
}