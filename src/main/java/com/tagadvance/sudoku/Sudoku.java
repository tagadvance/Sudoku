package com.tagadvance.sudoku;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

public interface Sudoku extends Copyable<Sudoku> {

	ImmutableSet<Character> getValues();

	ImmutableSet<Scope> getScopes();

	Set<Character> getPotentialValuesForCell(Grid grid, Cell cell);

	default boolean isValid(final Grid grid) {
		return getScopes().stream().allMatch(scope -> scope.isValid(grid));
	}

	default boolean isSolved(Grid grid) {
		return getScopes().stream().allMatch(scope -> scope.isSolved(grid));
	}

}
