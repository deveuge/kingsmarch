package com.deveuge.kingsmarch.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;

import com.deveuge.kingsmarch.domain.engine.Game;

@Configuration
public class EngineConfiguration {

    @Bean(name = "singleplayerGame")
    @SessionScope
    Game getSingleplayerGame() {
	    return new Game();
	}
    
}
