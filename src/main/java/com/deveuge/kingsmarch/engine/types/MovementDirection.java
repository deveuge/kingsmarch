package com.deveuge.kingsmarch.engine.types;

import com.deveuge.kingsmarch.engine.Square;

public enum MovementDirection {
	UP,
	DOWN,
	LEFT,
	RIGHT,
	UP_LEFT,
	UP_RIGHT,
	DOWN_LEFT,
	DOWN_RIGHT,
	UNIDENTIFIED;
	
	public static MovementDirection get(Square start, Square end) {
		int startRow = start.getRow();
		int startCol = start.getCol();
		int endRow = end.getRow();
		int endCol = end.getCol();
		
		if(startRow == endRow) {
			return startCol < endCol
					? RIGHT
					: LEFT;
		}
		if(startCol == endCol) {
			return startRow < endRow
					? UP
					: DOWN;
		}
		if(Math.abs(startRow - endRow) == Math.abs(startCol - endCol)) {
			if(startRow < endRow) {
				return startCol < endCol
						? UP_RIGHT
						: UP_LEFT;
			}
			if(startRow > endRow) {
				return startCol < endCol
						? DOWN_RIGHT
						: DOWN_LEFT;
			}
		}
		return UNIDENTIFIED;
	}
}
