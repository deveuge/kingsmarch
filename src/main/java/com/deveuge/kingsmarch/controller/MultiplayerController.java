package com.deveuge.kingsmarch.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.deveuge.kingsmarch.GameHelper;
import com.deveuge.kingsmarch.engine.Game;
import com.deveuge.kingsmarch.engine.GameId;
import com.deveuge.kingsmarch.engine.Move;
import com.deveuge.kingsmarch.engine.Player;
import com.deveuge.kingsmarch.engine.Position;
import com.deveuge.kingsmarch.engine.pieces.Piece;
import com.deveuge.kingsmarch.engine.types.Colour;
import com.deveuge.kingsmarch.security.StompPrincipal;
import com.deveuge.kingsmarch.websocket.ChatMessage;
import com.deveuge.kingsmarch.websocket.MessageType;
import com.deveuge.kingsmarch.websocket.MoveResponse;
import com.deveuge.kingsmarch.websocket.WebsocketHelper;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/mp")
public class MultiplayerController {
	
    @Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	@Autowired 
	private SimpUserRegistry simpUserRegistry;

	/**
	 * Multiplayer game view
	 * 
	 * Creates a new game and redirects the user to the game screen. The player must
	 * wait for the opponent to access the shared URL for the game to start.
	 * 
	 * @param model   {@link Model} Container that holds the data of the application
	 * @param id      {@link Optional}<{@link String}> Game ID:
	 *                <ul>
	 *                <li>Empty in case a new game is being created.</li>
	 *                <li>With value in case the opponent is accessing an already
	 *                created game.</li>
	 *                </ul>
	 * @param request {@link HttpServletRequest} HTTP request information
	 * @return {@link String} Multiplayer game view
	 */
	@GetMapping
    public String index(Model model, @RequestParam(required = false) Optional<String> id, HttpServletRequest request) {
		String gameId = id.isPresent() ? id.get() : GameId.generate();
        model.addAttribute("gameType", "multiplayer");
        model.addAttribute("uuid", gameId);
        model.addAttribute("requestURL", request.getRequestURL().toString());
        GameHelper.addGame(gameId, new Game());
        return "game";
    }
    
	/**
	 * Piece movement controller
	 * 
	 * Checks if the movement can be performed, updates the status of the current
	 * item and returns the data to the front end.
	 * 
	 * @param id     {@link String} Game ID
	 * @param source {@link String} Source square in algebraic notation
	 * @param target {@link String} Target square in algebraic notation
	 * @param colour {@link Colour} Player colour
	 * @return {@link MoveResponse}
	 */
    @PostMapping("move")
	public @ResponseBody MoveResponse move(String id, String source, String target, Colour colour) {
    	Game game = GameHelper.get(id);
		Player player = game.getPlayer(colour);
		
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
	 * @param id        {@link String} Game ID
	 * @param promotion {@link String} Piece to which the pawn is to be transformed
	 * @param colour    {@link Colour} Player colour
	 * @return {@link MoveResponse}
	 */
    @PostMapping("promote")
	public @ResponseBody MoveResponse promote(String id, String promotion, Colour colour) {
    	Game game = GameHelper.get(id);
		Player player = game.getPlayer(colour);
		Move move = game.getLastMove();
		if(!move.isPawnPromotion() || !player.getColour().equals(move.getPieceMoved().getColour())) {
			return new MoveResponse(false);
		}

		Piece piece = Piece.createPromotionPiece(promotion, colour);
		move.getEnd().setPiece(piece);
		MoveResponse response = new MoveResponse(true);
		response.setMoveData(game, move);
		response.setRefresh(true);
		return response;
    }
    
	/**
	 * Message filtering control in private game channels
	 * 
	 * Ensures that only messages from the two active players (black and white) are
	 * sent to the channel. 
	 * In case of a JOIN message, sends the current game status
	 * to reflect it on the board before starting the game.
	 * 
	 * @param id        {@link String} Game ID
	 * @param message   {@link ChatMessage} Message sent by Websocket
	 * @param principal {@link StompPrincipal} User who sent the message
	 */
    @MessageMapping("/chat.private.{id}")
    public void filterPrivateMessage(@DestinationVariable("id") String id, @Payload ChatMessage message,
    		StompPrincipal principal) {
    	if(MessageType.JOIN.equals(message.getType())) {
        	message.setContent(GameHelper.get(id).getBoard().getFEN());
        	message.setPlayers(WebsocketHelper.getUsersInChannel(simpUserRegistry, "/topic/" + id));
    	}
    	if(principal.getColour() != null) {
	    	message.setColour(principal.getColour());
	        simpMessagingTemplate.convertAndSend("/topic/" + id, message);
    	}
    }
}
