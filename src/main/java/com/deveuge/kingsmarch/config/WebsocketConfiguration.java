package com.deveuge.kingsmarch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.deveuge.kingsmarch.security.CustomHandshakeHandler;
import com.deveuge.kingsmarch.websocket.TopicSubscriptionInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfiguration implements WebSocketMessageBrokerConfigurer {
	
	private TopicSubscriptionInterceptor topicSubscriptionInterceptor;
	
	public WebsocketConfiguration(@Lazy TopicSubscriptionInterceptor topicSubscriptionInterceptor) {
		this.topicSubscriptionInterceptor = topicSubscriptionInterceptor;
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(topicSubscriptionInterceptor);
	}
	    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
        	.setHandshakeHandler(new CustomHandshakeHandler()).withSockJS();

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry
        	.setApplicationDestinationPrefixes("/app")
        	.enableSimpleBroker("/topic");
    }
    
}
