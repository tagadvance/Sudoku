package com.tagadvance.sudoku;

import static java.util.function.Predicate.not;

import com.google.common.collect.ImmutableSet;
import com.tagadvance.geometry.Point;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Scope {

	/**
	 * The positions this scope covers, which must hold distinct values. Geometry, not state: the
	 * same for every grid and every copy of one, which is why nothing here caches per grid.
	 */
	ImmutableSet<Point> getPoints();

	default Stream<Character> values(final Grid grid) {
		return getPoints().stream()
			.map(grid::getCellAt)
			.filter(not(Cell::isEmpty))
			.map(Cell::getValue);
	}

	default Collection<Character> getUsedValues(final Grid grid) {
		return values(grid).collect(Collectors.toSet());
	}

	default boolean isValid(final Grid grid) {
		final var seen = new HashSet<Character>();

		return values(grid).allMatch(seen::add);
	}

	default boolean isSolved(final Grid grid) {
		return values(grid).distinct().count() == getPoints().size();
	}

}
