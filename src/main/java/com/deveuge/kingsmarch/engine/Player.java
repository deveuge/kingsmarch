package com.deveuge.kingsmarch.engine;

import com.deveuge.kingsmarch.engine.types.Colour;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Player {
	
	public Colour colour;
	public boolean humanPlayer;
	
	public boolean isWhiteSide() {
		return colour.isWhite();
	}
	
}
