package com.tagadvance.sudoku;

import com.google.common.collect.ImmutableSet;
import com.tagadvance.geometry.ImmutablePoint;

public interface Sudoku<V> extends Copyable<Sudoku<V>> {

	ImmutableSet<V> getValues();

	ImmutableSet<Scope<V>> getScopes();

	ImmutableSet<Scope<V>> getScopesForPoint(ImmutablePoint point);

	boolean isValid(Grid<V> grid);

	boolean isSolved(Grid<V> grid);

}
