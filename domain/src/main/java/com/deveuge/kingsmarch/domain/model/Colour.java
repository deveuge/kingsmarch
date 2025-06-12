package com.deveuge.kingsmarch.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Colour {

	WHITE("w"),
	BLACK("b");
	
	public String notation;
	
	public boolean isWhite() {
		return WHITE.equals(this);
	}
	
	public Colour getOpposite() {
		return WHITE.equals(this) ? BLACK : WHITE;
	}
}
