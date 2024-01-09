package com.deveuge.kingsmarch.ai;

import com.deveuge.kingsmarch.engine.types.Colour;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Opening {
	
	private final String eco;
	private final String name;
	private final String fen;
	private final String[] moves;
	
	private final static String LETTERS = "hgfedcba";
	
	public Opening(String line, Colour colour) {
		String[] content = line.split("\\s*,\\s*");
		this.eco = content[0].replaceAll("\"", "");
		this.name = content[1].replaceAll("\"", "");
		this.fen = content[2].replaceAll("\"", "");
		this.moves = parseMoves(content[3].replaceAll("\"", ""), colour.isWhite());
	}
	
	private String[] parseMoves(String movesString, boolean isWhite) {
		String[] moves = movesString.split(" ");
		for(int i = 0; i < moves.length; i++) {
			String start = moves[i].substring(0, 2);
			String end = moves[i].substring(2);
			moves[i] = isWhite
					? String.format("%s-%s", start, end)
					: String.format("%s-%s", invertNotation(start), invertNotation(end));
		}
		return moves;
	}
	
	private String invertNotation(String source) {
		int col = Integer.valueOf(source.charAt(0) - 'a');
		int row = 9 - Integer.valueOf(source.substring(1));
		return String.format("%s%s", LETTERS.charAt(col), row);
	}

}
