package com.deveuge.kingsmarch.engine.types;

public enum Colour {

	WHITE,
	BLACK;
	
	public boolean isWhite() {
		return WHITE.equals(this);
	}
	
	public Colour getOpposite() {
		return WHITE.equals(this) ? BLACK : WHITE;
	}
}
