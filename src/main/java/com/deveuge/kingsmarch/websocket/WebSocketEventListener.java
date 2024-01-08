package com.deveuge.kingsmarch.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.deveuge.kingsmarch.engine.util.GameHelper;
import com.deveuge.kingsmarch.security.StompPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
	
	@Autowired 
	private SimpUserRegistry simpUserRegistry;
	private final SimpMessageSendingOperations messageTemplate;

	@EventListener
	public void handleDisconnectEvent(SessionDisconnectEvent event) {
		StompPrincipal principal = (StompPrincipal) event.getUser();
        
		int usersInChannel = WebsocketHelper.getUsersInChannel(simpUserRegistry, "/topic/" + principal.getGameId());
		if(usersInChannel == 0) {
			GameHelper.removeGame(principal.getGameId());
		}
		
		String username = principal.getName();
		if(StringUtils.hasText(username)) {
			log.info("User disconnected: {}", username);
			var chatMessage = ChatMessage.builder()
					.type(MessageType.LEAVE)
					.build();
			messageTemplate.convertAndSend("/topic/" + principal.getGameId(), chatMessage);
		}
	}
}
