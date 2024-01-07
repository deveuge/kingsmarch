package com.deveuge.kingsmarch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.deveuge.kingsmarch.GameAI;
import com.deveuge.kingsmarch.engine.Game;
import com.deveuge.kingsmarch.engine.Move;
import com.deveuge.kingsmarch.engine.Player;
import com.deveuge.kingsmarch.engine.Position;
import com.deveuge.kingsmarch.engine.pieces.Piece;
import com.deveuge.kingsmarch.engine.types.Colour;
import com.deveuge.kingsmarch.websocket.MoveResponse;

@Controller
@RequestMapping("/sp")
public class SingleplayerController {

	@Autowired
	@Qualifier("singleplayerGame")
	Game game;
	
	@GetMapping
	public String index(Model model) {
        model.addAttribute("gameType", "singleplayer");
        model.addAttribute("gameFEN", game.getBoard().getFEN());
		return "game";
	}
	
	@GetMapping("new")
	public String newGame(Model model) {
		game = new Game();
		return "redirect:/sp";
	}
	
	@PostMapping("move")
	public @ResponseBody MoveResponse move(String source, String target) {
		Player player = game.getPlayer(Colour.WHITE);
		
		boolean moveCorrect = game.move(player, new Position(source), new Position(target));
		MoveResponse response = new MoveResponse(moveCorrect);
		if(moveCorrect) {
			Move move = game.getLastMove();
			response.setMoveData(game, move);
		}
		return response;
	}
	
	@PostMapping("promote")
	public @ResponseBody MoveResponse promote(String promotion) {
		Player player = game.getPlayer(Colour.WHITE);
		Move move = game.getLastMove();
		if(!move.isPawnPromotion() || !player.getColour().equals(move.getPieceMoved().getColour())) {
			return new MoveResponse(false);
		}

		Piece piece = Piece.createPromotionPiece(promotion, Colour.WHITE);
		move.getEnd().setPiece(piece);
		MoveResponse response = new MoveResponse(true);
		response.setMoveData(game, move);
		response.setRefresh(true);
		return response;
    }

	@PostMapping("automove")
	public @ResponseBody MoveResponse autoMove() {
		Player player = game.getPlayer(Colour.BLACK);
		Move bestMove = GameAI.getBestMove(game);
		Position start = new Position(bestMove.getStart());
		Position end = new Position(bestMove.getEnd());
		game.move(player, start, end);
		
		MoveResponse response = new MoveResponse(true);
		response.setMoveData(game, bestMove);
		response.setMove(String.format("%s-%s", start.getAlgebraicNotation(), end.getAlgebraicNotation()));
		return response;
	}
}
