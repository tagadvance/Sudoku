package com.tagadvance.sudoku;

import static java.util.function.Predicate.not;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: replace with array reference
public interface Scope {

	default Stream<Character> values(Grid grid) {
		return getCells(grid).stream().filter(not(Cell::isEmpty)).map(Cell::getValue);
	}

	Collection<Cell> getCells(Grid grid);

	default Collection<Character> getUsedValues(final Grid grid) {
		return values(grid).collect(Collectors.toSet());
	}

	default boolean isValid(final Grid grid) {
		final var set = new HashSet<>();

		return values(grid).allMatch(set::add);
	}

	default boolean isSolved(final Grid grid) {
		final var cells = getCells(grid);

		return values(grid).distinct().count() == cells.size();
	}

}
