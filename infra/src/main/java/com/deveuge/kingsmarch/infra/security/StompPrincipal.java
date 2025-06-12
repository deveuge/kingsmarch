package com.deveuge.kingsmarch.infra.security;

import java.security.Principal;

import com.deveuge.kingsmarch.domain.model.Colour;
import com.deveuge.kingsmarch.domain.model.GameId;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StompPrincipal implements Principal {
	
    private String name;
    private GameId gameId;
    private Colour colour;
    
	public StompPrincipal(String name) {
		super();
		this.name = name;
	}
	
}