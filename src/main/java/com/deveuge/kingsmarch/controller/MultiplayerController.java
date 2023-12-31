package com.deveuge.kingsmarch.controller;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSubscription;
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
import com.deveuge.kingsmarch.engine.Player;
import com.deveuge.kingsmarch.engine.Position;
import com.deveuge.kingsmarch.engine.types.Colour;
import com.deveuge.kingsmarch.security.StompPrincipal;
import com.deveuge.kingsmarch.websocket.ChatMessage;
import com.deveuge.kingsmarch.websocket.MessageType;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/mp")
public class MultiplayerController {
	
    @Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	@Autowired 
	private SimpUserRegistry simpUserRegistry;

	@GetMapping
    public String index(Model model, @RequestParam(required = false) Optional<String> id, HttpServletRequest request) {
		String gameId = id.isPresent() ? id.get() : GameId.generate();
        model.addAttribute("gameType", "multiplayer");
        model.addAttribute("uuid", gameId);
        model.addAttribute("servletPath", request.getRequestURL().toString());
        GameHelper.addGame(gameId, new Game());
        return "game";
    }
    
    @PostMapping("move")
	public @ResponseBody String move(String id, String source, String target, Colour colour) {
    	Game game = GameHelper.get(id);
		Player player = game.getPlayer(colour);
		Position startPosition = new Position(source);
		Position endPosition = new Position(target);
		
		boolean moveCorrect = game.playerMove(player, startPosition.getRow(), startPosition.getCol(), endPosition.getRow(), endPosition.getCol());
		return moveCorrect ? "ok" : "snapback";
	}
    
    @MessageMapping("/chat.private.{id}")
    public void filterPrivateMessage(@DestinationVariable("id") String id, @Payload ChatMessage message,
    		StompPrincipal principal) {
    	if(MessageType.JOIN.equals(message.getType())) {
    		Set<SimpSubscription> matches = simpUserRegistry.findSubscriptions(s -> s.getDestination().equals("/topic/" + principal.getGameId()));
        	message.setContent(String.valueOf(matches.size()));
    	}
    	if(principal.getColour() != null) {
	    	message.setColour(principal.getColour());
	        simpMessagingTemplate.convertAndSend("/topic/" + id, message);
    	}
    }
}
