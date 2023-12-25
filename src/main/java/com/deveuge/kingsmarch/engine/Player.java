package com.deveuge.kingsmarch.engine;

import com.deveuge.kingsmarch.engine.types.Colour;

import lombok.Getter;

@Getter
public abstract class Player {
	
	public Colour colour;
	public boolean humanPlayer;
	
	public boolean isWhiteSide() {
		return colour.isWhite();
	}
	
}
