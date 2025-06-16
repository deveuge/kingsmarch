package com.deveuge.kingsmarch.domain.model;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Difficulty {
	BEGINNER(0, 2, false), 
	INTERMEDIATE(1, 4, true), 
	ADVANCED(2, 6, true), 
	EXPERT(3, 8, true);

	private final int index;
	private final int depth;
	private final boolean useTranspositionTable;

	private static final Map<Integer, Difficulty> INDEX_MAP = Arrays.stream(Difficulty.values())
			.collect(Collectors.toMap(d -> d.index, d -> d));

	public int getDepth() {
		return depth;
	}

	public boolean useTranspositionTable() {
		return useTranspositionTable;
	}
	
	public int getIndex() {
		return index;
	}
	
	public static Difficulty getDefault() {
		return INTERMEDIATE;
	}
	
	public static Difficulty fromIndex(Optional<Integer> index) {
		return index.isEmpty()
				? getDefault()
				: INDEX_MAP.getOrDefault(index.get(), getDefault());
	}
}