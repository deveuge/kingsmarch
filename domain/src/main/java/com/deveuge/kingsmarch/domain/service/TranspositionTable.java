package com.deveuge.kingsmarch.domain.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Transposition Table implementation for chess AI. 
 * Stores previously computed positions to avoid redundant calculations.
 */
public class TranspositionTable {

	private final ConcurrentHashMap<String, TranspositionEntry> table;
	private final int maxSize;
	private final AtomicLong hits = new AtomicLong(0);
	private final AtomicLong misses = new AtomicLong(0);
	private int currentAge = 0;

	public TranspositionTable(int maxSize) {
		this.maxSize = maxSize;
		this.table = new ConcurrentHashMap<>(maxSize);
	}

	/**
	 * Stores a position evaluation in the table.
	 */
	public void store(String positionKey, int value, int depth, TranspositionFlag flag) {
		// If table is full, perform cleanup
		if (table.size() >= maxSize) {
			cleanup();
		}

		TranspositionEntry existing = table.get(positionKey);

		// Replace if: no existing entry, deeper search, or same depth but newer
		if (existing == null || depth > existing.getDepth()
				|| (depth == existing.getDepth() && currentAge > existing.getAge())) {

			TranspositionEntry entry = new TranspositionEntry(value, depth, flag, currentAge);
			table.put(positionKey, entry);
		}
	}

	/**
	 * Looks up a position in the table.
	 */
	public TranspositionEntry lookup(String positionKey) {
		TranspositionEntry entry = table.get(positionKey);

		if (entry != null) {
			hits.incrementAndGet();
			return entry;
		} else {
			misses.incrementAndGet();
			return null;
		}
	}

	/**
	 * Increments the age counter for replacement strategy.
	 */
	public void incrementAge() {
		currentAge++;
	}

	/**
	 * Clears the table.
	 */
	public void clear() {
		table.clear();
		hits.set(0);
		misses.set(0);
		currentAge = 0;
	}

	/**
	 * Gets the hit rate of the table.
	 */
	public double getHitRate() {
		long totalAccesses = hits.get() + misses.get();
		return totalAccesses > 0 ? (double) hits.get() / totalAccesses : 0.0;
	}

	/**
	 * Gets table statistics.
	 */
	public TableStats getStats() {
		return new TableStats(table.size(), maxSize, hits.get(), misses.get(), getHitRate());
	}

	/**
	 * Cleanup old entries when table is full. 
	 * Removes entries with older age and lower depth.
	 */
	private void cleanup() {
		int targetSize = maxSize * 3 / 4; // Remove 25% of entries

		table.entrySet().removeIf(entry -> {
			TranspositionEntry value = entry.getValue();
			// Remove entries that are old and have low depth
			return value.getAge() < currentAge - 2 && value.getDepth() < 4;
		});

		// If still too full, remove more aggressively
		if (table.size() > targetSize) {
			table.entrySet().removeIf(entry -> {
				TranspositionEntry value = entry.getValue();
				return value.getAge() < currentAge - 1;
			});
		}
	}

	/**
	 * Statistics class for the transposition table.
	 */
	@Getter
	public static class TableStats {
		private final int currentSize;
		private final int maxSize;
		private final long hits;
		private final long misses;
		private final double hitRate;

		public TableStats(int currentSize, int maxSize, long hits, long misses, double hitRate) {
			this.currentSize = currentSize;
			this.maxSize = maxSize;
			this.hits = hits;
			this.misses = misses;
			this.hitRate = hitRate;
		}

		@Override
		public String toString() {
			return String.format("TT Stats - Size: %d/%d, Hits: %d, Misses: %d, Hit Rate: %.2f%%", currentSize, maxSize,
					hits, misses, hitRate * 100);
		}
	}
}

/**
 * Entry in the transposition table.
 */
@RequiredArgsConstructor
@Getter
class TranspositionEntry {
	private final int value;
	private final int depth;
	private final TranspositionFlag flag;
	private final int age;
}

/**
 * Flags for transposition table entries.
 */
enum TranspositionFlag {
	EXACT, // Exact value
	LOWER_BOUND, // Value is at least this (alpha cutoff)
	UPPER_BOUND // Value is at most this (beta cutoff)
}
