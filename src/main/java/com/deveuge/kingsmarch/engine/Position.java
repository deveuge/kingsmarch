package com.deveuge.kingsmarch.engine;

import lombok.Getter;

@Getter
public class Position {
	
	int row;
	int col;
	
	public Position(String source) {
		super();
		this.row = source.charAt(1) - '1';
		this.col = source.charAt(0) - 'a';
	}
	
}
