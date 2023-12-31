package com.deveuge.kingsmarch.websocket;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.deveuge.kingsmarch.security.StompPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
	
	private final SimpMessageSendingOperations messageTemplate;

	@EventListener
	public void handleDisconnectEvent(SessionDisconnectEvent event) {
		StompPrincipal principal = (StompPrincipal) event.getUser();
        
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
