package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.tagadvance.geometry.Dimension;
import com.tagadvance.geometry.Point;
import java.util.Set;

/**
 * A grid of values addressed by position, where a position is the index {@code y * width + x}.
 * There is no cell object: a cell is a {@code char} at an index, which is what makes a copy an
 * array clone rather than a walk over eighty-one objects.
 */
public interface Grid extends Copyable<Grid> {

	/**
	 * The value of a position that has not been filled in. NUL is not a legal puzzle character, so
	 * no value alphabet can collide with it, and it is what a {@code char} array is born holding.
	 */
	char EMPTY = '\0';

	Dimension getSize();

	/**
	 * Returns the number of positions, which is width times height.
	 */
	int size();

	char get(int position);

	void set(int position, char value);

	default boolean isEmpty(final int position) {
		return get(position) == EMPTY;
	}

	default int positionOf(final Point point) {
		return (point.y() * getSize().width()) + point.x();
	}

	default char get(final Point point) {
		return get(positionOf(point));
	}

	default void set(final Point point, final char value) {
		set(positionOf(point), value);
	}

	default int countEmpty() {
		int count = 0;
		for (int position = 0, size = size(); position < size; position++) {
			if (isEmpty(position)) {
				count++;
			}
		}

		return count;
	}

	/**
	 * Fills this grid from a puzzle written in row-major order, one character per position. A
	 * character in {@code values} is a value; anything else is a blank, so '0', '.' and '?' all
	 * work without being configured.
	 *
	 * @param puzzle exactly one character per position, rows first
	 * @param values the alphabet; every other character reads as a blank
	 */
	default void populate(final String puzzle, final Set<Character> values) {
		checkNotNull(puzzle, "puzzle must not be null");
		checkNotNull(values, "values must not be null");

		final int size = size();
		checkArgument(puzzle.length() == size, "puzzle must be %s characters, but was %s", size,
			puzzle.length());

		for (int position = 0; position < size; position++) {
			final char c = puzzle.charAt(position);
			if (values.contains(c)) {
				set(position, c);
			}
		}
	}

}
