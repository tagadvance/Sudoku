package com.tagadvance.sudoku;

import java.util.Collection;

public interface Scope<V> {

	Collection<Cell<V>> getCells(Grid<V> grid);

	Collection<V> getUsedValues(Grid<V> grid);

	boolean isValid(Grid<V> grid);

	boolean isSolved(Grid<V> grid);

}
