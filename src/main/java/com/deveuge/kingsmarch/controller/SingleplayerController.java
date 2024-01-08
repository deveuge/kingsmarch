package com.deveuge.kingsmarch.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.deveuge.kingsmarch.ai.GameAI;
import com.deveuge.kingsmarch.engine.Board;
import com.deveuge.kingsmarch.engine.Game;
import com.deveuge.kingsmarch.engine.Move;
import com.deveuge.kingsmarch.engine.Player;
import com.deveuge.kingsmarch.engine.pieces.Piece;
import com.deveuge.kingsmarch.engine.types.Colour;
import com.deveuge.kingsmarch.engine.util.Position;
import com.deveuge.kingsmarch.websocket.MoveResponse;

@Controller
@RequestMapping("/sp")
public class SingleplayerController {

	@Autowired
	@Qualifier("singleplayerGame")
	Game game;

	/**
	 * Single-player game view
	 * 
	 * @param model {@link Model} Container that holds the data of the application
	 * @param fen   {@link Optional}<{@link String}> FEN of the initial board state
	 * @return {@link String} Single-player game view
	 */
	@GetMapping
	public String index(Model model, @RequestParam Optional<String> fen) {
		if(fen.isPresent()) {
        	game.setBoard(new Board(fen.get()));
        }
        model.addAttribute("gameType", "singleplayer");
        model.addAttribute("gameFEN", game.getBoard().getFEN());
		return "game";
	}
	
	/**
	 * Restarts the game stored in session
	 * 
	 * @param model {@link Model} Container that holds the data of the application
	 * @return {@link String} Single-player game view
	 */
	@GetMapping("new")
	public String newGame(Model model) {
		game = new Game();
		return "redirect:/sp";
	}
	
	/**
	 * Piece movement controller
	 * 
	 * Checks if the movement can be performed, updates the status of the current
	 * item and returns the data to the front end.
	 * 
	 * @param source {@link String} Source square in algebraic notation
	 * @param target {@link String} Target square in algebraic notation
	 * @return {@link MoveResponse}
	 */
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
	
	/**
	 * Pawn promotion controller
	 * 
	 * Promotes the pawn to the selected piece.
	 * 
	 * @param promotion {@link String} Piece to which the pawn is to be transformed
	 * @return {@link MoveResponse}
	 */
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

	/**
	 * Computer move controller
	 * 
	 * Calculates the next move.
	 * 
	 * @return {@link MoveResponse}
	 */
	@PostMapping("automove")
	public @ResponseBody MoveResponse autoMove() {
		Player player = game.getPlayer(Colour.BLACK);
		Move bestMove = GameAI.getNextMove(game.getBoard());
		Position start = new Position(bestMove.getStart());
		Position end = new Position(bestMove.getEnd());
		game.move(player, start, end);
		
		MoveResponse response = new MoveResponse(true);
		response.setMoveData(game, bestMove);
		response.setMove(String.format("%s-%s", start.getAlgebraicNotation(), end.getAlgebraicNotation()));
		return response;
	}
}
