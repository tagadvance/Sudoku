package com.tagadvance.sudoku;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.tagadvance.geometry.ImmutablePoint;

public interface Scope<V> {

	ImmutableSet<ImmutablePoint> getCellPoints();

	Set<V> getUsedValues(Grid<V> grid);

	boolean isValid(Grid<V> grid);

	boolean isSolved(Grid<V> grid);

}
