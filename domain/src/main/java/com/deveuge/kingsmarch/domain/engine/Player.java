package com.deveuge.kingsmarch.domain.engine;

import com.deveuge.kingsmarch.domain.engine.types.Colour;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Player {
	
	public Colour colour;
	
	public boolean isWhiteSide() {
		return colour.isWhite();
	}
	
}
