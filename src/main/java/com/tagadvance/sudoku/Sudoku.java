package com.tagadvance.sudoku;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

public interface Sudoku<V> extends Copyable<Sudoku<V>> {

	ImmutableSet<V> getValues();

	ImmutableSet<Scope<V>> getScopes();

	Set<V> getPotentialValuesForCell(Grid<V> grid, Cell<V> cell);

	default boolean isValid(final Grid<V> grid) {
		return getScopes().stream().allMatch(scope -> scope.isValid(grid));
	}

	default boolean isSolved(Grid<V> grid) {
		return getScopes().stream().allMatch(scope -> scope.isSolved(grid));
	}

}
