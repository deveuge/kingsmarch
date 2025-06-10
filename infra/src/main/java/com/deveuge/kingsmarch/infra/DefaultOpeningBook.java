package com.deveuge.kingsmarch.infra;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.deveuge.kingsmarch.domain.ai.Opening;
import com.deveuge.kingsmarch.domain.ai.OpeningBook;
import com.deveuge.kingsmarch.domain.engine.Move;

public class DefaultOpeningBook implements OpeningBook {

	private final List<Opening> openings;
	private final int OPENING_MOVES_MAXLENGTH = 10;
	private final Random random = new Random();

	public DefaultOpeningBook(List<Opening> openings) {
		this.openings = openings;
	}

	@Override
	public List<Move> getNext(List<Move> historic, List<Move> calculatedMoves) {
		int size = historic.size();
		if (size >= OPENING_MOVES_MAXLENGTH) {
			return null;
		}

		List<Move> openingMoves = getOpenings(historic, calculatedMoves);
		if (openingMoves.isEmpty()) {
			return null;
		}

		if (size > 1) {
			return openingMoves;
		}

		int randomIndex = random.nextInt(openingMoves.size());
		return new ArrayList<>(List.of(openingMoves.get(randomIndex)));
	}

	private List<Move> getOpenings(List<Move> historic, List<Move> calculatedMoves) {
		int position = historic.size();
		List<Move> openingMoves = new LinkedList<>();
		for (Move move : calculatedMoves) {
			for (Opening opening : openings) {
				if (position < opening.getMoves().length
						&& (historic.isEmpty() || matchesHistoric(historic, opening.getMoves(), position))
						&& (move.getAlgebraicNotation().equals(opening.getMoves()[position]))) {
					openingMoves.add(move);
				}
			}
		}
		return openingMoves;
	}

	private boolean matchesHistoric(List<Move> historic, String[] moves, int position) {
		for (int i = position - 1; i >= 0; i--) {
			if (!historic.get(i).getAlgebraicNotation().equals(moves[i])) {
				return false;
			}
		}
		return true;
	}
}