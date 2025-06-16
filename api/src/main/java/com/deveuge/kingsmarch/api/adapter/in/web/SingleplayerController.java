package com.deveuge.kingsmarch.api.adapter.in.web;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.deveuge.kingsmarch.app.usecase.MakeAIMoveUseCase;
import com.deveuge.kingsmarch.app.usecase.MakePlayerMoveUseCase;
import com.deveuge.kingsmarch.app.usecase.PromotePawnUseCase;
import com.deveuge.kingsmarch.app.usecase.StartSingleplayerGameUseCase;
import com.deveuge.kingsmarch.domain.engine.Game;
import com.deveuge.kingsmarch.domain.model.Difficulty;
import com.deveuge.kingsmarch.infra.messaging.MoveResponse;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/sp")
@RequiredArgsConstructor
public class SingleplayerController {

	private final StartSingleplayerGameUseCase startGame;
    private final MakePlayerMoveUseCase makePlayerMove;
    private final PromotePawnUseCase promotePawn;
    private final MakeAIMoveUseCase makeAIMove;

	/**
	 * Single-player game view
	 * 
	 * @param model {@link Model} Container that holds the data of the application
	 * @param fen   {@link Optional}<{@link String}> FEN of the initial board state
	 * @return {@link String} Single-player game view
	 */
	@GetMapping
	public String index(Model model, @RequestParam Optional<Integer> difficulty, @RequestParam Optional<String> fen) {
        Game game = startGame.startNewGame(Difficulty.fromIndex(difficulty), fen);
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
		return "redirect:/sp?difficulty=" + startGame.getDifficulty().getIndex();
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
		return makePlayerMove.makeMove(source, target);
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
		return promotePawn.promote(promotion);
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
		return makeAIMove.makeMove();
	}
}
