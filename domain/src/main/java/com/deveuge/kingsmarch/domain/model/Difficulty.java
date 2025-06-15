package com.deveuge.kingsmarch.domain.model;

public enum Difficulty {
	BEGINNER(2, false),
	INTERMEDIATE(4, true),
	ADVANCED(6, true),
	EXPERT(8, true);
	
	private final int depth;
	private final boolean useTranspositionTable;
	
	Difficulty(int depth, boolean useTranspositionTable) {
		this.depth = depth;
		this.useTranspositionTable = useTranspositionTable;
	}
	
	public int getDepth() { return depth; }
	public boolean useTranspositionTable() { return useTranspositionTable; }
}