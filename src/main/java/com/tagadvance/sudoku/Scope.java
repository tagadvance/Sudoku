package com.tagadvance.sudoku;

import static java.util.function.Predicate.not;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Scope<V> {

	default Stream<V> values(Grid<V> grid) {
		return getCells(grid).stream().filter(not(Cell::isEmpty)).map(Cell::getValue);
	}

	Collection<Cell<V>> getCells(Grid<V> grid);

	default Collection<V> getUsedValues(final Grid<V> grid) {
		return values(grid).collect(Collectors.toSet());
	}

	default boolean isValid(final Grid<V> grid) {
		final var set = new HashSet<>();

		return values(grid).allMatch(set::add);
	}

	default boolean isSolved(final Grid<V> grid) {
		final var cells = getCells(grid);

		return values(grid).distinct().count() == cells.size();
	}

}
