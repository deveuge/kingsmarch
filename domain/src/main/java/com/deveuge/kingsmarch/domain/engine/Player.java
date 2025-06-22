package com.deveuge.kingsmarch.domain.engine;

import com.deveuge.kingsmarch.domain.model.Colour;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Player {
	
	public Colour colour;
	
	public boolean isWhiteSide() {
		return colour.isWhite();
	}
	
	public String getFEN() {
		return Colour.WHITE.equals(this.colour) ? "w" : "b";
	}
}
