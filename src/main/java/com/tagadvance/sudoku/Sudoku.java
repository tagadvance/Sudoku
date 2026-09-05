package com.tagadvance.sudoku;

import com.google.common.collect.ImmutableSet;

public interface Sudoku extends Copyable<Sudoku> {

	ImmutableSet<Character> getValues();

	ImmutableSet<Scope> getScopes();

	/**
	 * Returns the values that could still go at this position, as a bitmask: bit k is set when the
	 * symbol with ordinal k is still available. A mask rather than a set because this is the hot
	 * path -- a solve asks tens of thousands of times, and {@link Integer#bitCount} answers "how
	 * constrained is this position" in one instruction.
	 */
	int candidates(Grid grid, int position);

	/**
	 * Returns the symbol with the given ordinal, which is the bit position used by
	 * {@link #candidates}.
	 */
	char symbol(int ordinal);

	boolean isValid(Grid grid);

	boolean isSolved(Grid grid);

}
