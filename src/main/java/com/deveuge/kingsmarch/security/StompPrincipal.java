package com.deveuge.kingsmarch.security;

import java.security.Principal;

import com.deveuge.kingsmarch.engine.types.Colour;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StompPrincipal implements Principal {
	
    private String name;
    private String gameId;
    private Colour colour;
    
	public StompPrincipal(String name) {
		super();
		this.name = name;
	}
	
}