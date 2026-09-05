package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableCollection;
import com.tagadvance.geometry.Dimension;
import com.tagadvance.geometry.Point;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface Grid extends Copyable<Grid> {

	/**
	 * Fills this grid from a puzzle written in row-major order, one character per cell. A character
	 * in {@code values} is a value; anything else is a blank, so '0', '.' and '?' all work without
	 * being configured.
	 *
	 * @param puzzle exactly one character per cell, rows first
	 * @param values the alphabet; every other character reads as a blank
	 */
	default void populate(final String puzzle, final Set<Character> values) {
		checkNotNull(puzzle, "puzzle must not be null");
		checkNotNull(values, "values must not be null");

		final var size = getSize();
		final int width = size.width(), cellCount = width * size.height();
		checkArgument(puzzle.length() == cellCount, "puzzle must be %s characters, but was %s",
			cellCount, puzzle.length());

		for (int i = 0; i < cellCount; i++) {
			final char c = puzzle.charAt(i);
			if (values.contains(c)) {
				getCellAt(new Point(i % width, i / width)).setValue(c);
			}
		}
	}

	Dimension getSize();

	/**
	 * @return the empty cells, in a list the caller is free to sort and consume -- the solvers do
	 * both
	 */
	default List<Cell> getEmptyCells() {
		return getCells()
			.stream()
			.filter(Cell::isEmpty)
			.collect(Collectors.toList());
	}

	ImmutableCollection<Cell> getCells();

	Cell getCellAt(Point point);

}
