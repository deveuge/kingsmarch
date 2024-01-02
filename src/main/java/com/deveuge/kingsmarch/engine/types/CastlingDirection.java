package com.deveuge.kingsmarch.engine.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum to differentiate castling direction:
 * <ul>
 * <li>Castling kingside (short castling) consists of moving the king to g1 and
 * the rook to f1 for White, or moving the king to g8 and the rook to f8 for
 * Black.</li>
 * <li>Castling queenside (long castling) consists of moving the king to c1 and
 * the rook to d1 for White, or moving the king to c8 and the rook to d8 for
 * Black.</li>
 * </ul>
 */
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
