package com.deveuge.kingsmarch.engine.util;

import com.deveuge.kingsmarch.engine.Square;

import lombok.Getter;

@Getter
public class Position {
	
	int row;
	int col;
	String algebraicNotation;
	
	/**
	 * Transforms the received algebraic notation into array coordinates.
	 * 
	 * @param source {@link String} Source square in algebraic notation
	 */
	public Position(String source) {
		super();
		this.row = source.charAt(1) - '1';
		this.col = source.charAt(0) - 'a';
		this.algebraicNotation = source;
	}
	
	public Position(Square square) {
		super();
		this.algebraicNotation = String.format("%s%s", (char) ('A' + square.getCol()), square.getRow() + 1)
				.toLowerCase();
		this.row = square.getRow();
		this.col = square.getCol();
	}
	
}
