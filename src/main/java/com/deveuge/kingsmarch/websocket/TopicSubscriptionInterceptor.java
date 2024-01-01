package com.deveuge.kingsmarch.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.deveuge.kingsmarch.security.StompPrincipal;

@Component
public class TopicSubscriptionInterceptor implements ChannelInterceptor {
	
	@Autowired 
	private SimpUserRegistry simpUserRegistry;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
		if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
			StompPrincipal principal = (StompPrincipal) headerAccessor.getUser();
			String destination = headerAccessor.getDestination();
			int usersInChannel = WebsocketHelper.getUsersInChannel(simpUserRegistry, destination);
			if (usersInChannel >= 2) {
				throw new IllegalArgumentException("No permission for this topic");
			}
			principal.setGameId(destination.replace("/topic/", ""));
			principal.setColour(WebsocketHelper.getNewColour(simpUserRegistry, destination));
			
		}
		return message;
	}

}
