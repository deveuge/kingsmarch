package com.deveuge.kingsmarch.websocket;

import java.util.Set;

import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUserRegistry;

import com.deveuge.kingsmarch.engine.types.Colour;
import com.deveuge.kingsmarch.security.StompPrincipal;

public class WebsocketHelper {

	public static int getUsersInChannel(SimpUserRegistry simpUserRegistry, String topicDestination) {
		Set<SimpSubscription> matches = simpUserRegistry
				.findSubscriptions(s -> s.getDestination().equals(topicDestination));
		return matches.size();
	}
	
	public static Colour getNewColour(SimpUserRegistry simpUserRegistry, String topicDestination) {
		Set<SimpSubscription> matches = simpUserRegistry
				.findSubscriptions(s -> s.getDestination().equals(topicDestination));
		if(matches.isEmpty()) {
			return Colour.WHITE;
		}
		return ((StompPrincipal) matches.iterator().next().getSession().getUser().getPrincipal()).getColour().getOpposite();
	}
}
