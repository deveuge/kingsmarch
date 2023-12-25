package com.deveuge.kingsmarch.engine.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CastlingDirection {

	KINGSIDE(7), 
	QUEENSIDE(0);

	private int col;
	
	public int getEndingKingCol() {
		return KINGSIDE.equals(this) 
				? this.col - 1
				: this.col + 1;
	}
	
	public int getEndingRookCol() {
		return KINGSIDE.equals(this) 
				? this.col - 2
				: this.col + 2;
	}

	public static CastlingDirection get(int column) {
		for (CastlingDirection direction : values()) {
			if (direction.col == column) {
				return direction;
			}
		}
		return null;
	}
}
