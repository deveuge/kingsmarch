package com.deveuge.kingsmarch.ai;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.deveuge.kingsmarch.engine.Move;

import jakarta.annotation.PostConstruct;

@Component
public class OpeningBook {

	private static List<Opening> openings = new ArrayList<>();
	private static final int OPENING_MOVES_MAXLENGTH = 10;
	private static Random random = new Random();

	/**
	 * Reads the Openings.txt file to keep the Opening Book in memory.
	 */
	@PostConstruct
	public static void init() {
        try {
            File file = ResourceUtils.getFile("classpath:Openings.txt");
            Stream<String> lines = Files.lines(file.toPath());
            lines.forEach(l -> openings.add(new Opening(l, GameAI.AI_COLOUR)));
            lines.close();
        } catch (IOException e) {
			e.printStackTrace();
        }
	}
	
	/**
	 * Gets the next opening movements
	 * 
	 * @param historic        {@link List}<{@link Move}> List of movements made by the player
	 * @param calculatedMoves {@link List}<{@link Move}> List of possible movements
	 * @return {@link List}<{@link Move}>
	 */
	public static List<Move> getNext(List<Move> historic, List<Move> calculatedMoves) {
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
		return new ArrayList<>(Arrays.asList(openingMoves.get(randomIndex)));
	}
	
	/**
	 * Gets the opening movements
	 * 
	 * @param historic        {@link List}<{@link Move}> List of movements made by the player
	 * @param calculatedMoves {@link List}<{@link Move}> List of possible movements
	 * @return {@link List}<{@link Move}>
	 */
	private static List<Move> getOpenings(List<Move> historic, List<Move> calculatedMoves) {
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
	
	/**
	 * Check if the succession of movements corresponds to the opening
	 * 
	 * @param historic {@link List}<{@link Move}> List of movements made by the player
	 * @param notation {@link String}[] List of movements that are part of the opening
	 * @param position int Position index within the opening
	 * @return true if matches the historic, false otherwise
	 */
	private static boolean matchesHistoric(List<Move> historic, String[] moves, int position) {
		for (int i = position - 1; i >= 0; i--) {
			if (!historic.get(i).getAlgebraicNotation().equals(moves[i])) {
				return false;
			}
		}
		return true;
	}
}
