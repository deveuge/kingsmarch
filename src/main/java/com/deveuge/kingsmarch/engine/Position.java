package com.deveuge.kingsmarch.engine;

import lombok.Getter;

@Getter
public class Position {
	
	int row;
	int col;
	
	/**
	 * Transforms the received algebraic notation into array coordinates.
	 * 
	 * @param source {@link String} Source square in algebraic notation
	 */
	public Position(String source) {
		super();
		this.row = source.charAt(1) - '1';
		this.col = source.charAt(0) - 'a';
	}
	
}
