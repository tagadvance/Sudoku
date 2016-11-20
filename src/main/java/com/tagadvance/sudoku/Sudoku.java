package com.tagadvance.sudoku;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public interface Sudoku<V> extends Copyable<Sudoku<V>> {

	ImmutableSet<V> getValues();

	ImmutableSet<Scope<V>> getScopes();

	Set<V> getPotentialValuesForCell(Grid<V> grid, Cell<V> cell);

	boolean isValid(Grid<V> grid);

	boolean isSolved(Grid<V> grid);

}
