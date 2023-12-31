package com.deveuge.kingsmarch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import com.deveuge.kingsmarch.engine.Game;

@Configuration
public class EngineConfiguration {

    @Bean(name = "singleplayerGame")
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    Game getSingleplayerGame() {
	    return new Game();
	}
    
}
