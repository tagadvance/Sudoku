package com.tagadvance.sudoku;

import com.google.common.collect.ImmutableCollection;
import com.tagadvance.geometry.Dimension;
import com.tagadvance.geometry.Point;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface Grid extends Copyable<Grid> {

	default void populate(final String puzzle) {
		final var size = getSize();

		final var x = new AtomicInteger();
		final var y = new AtomicInteger();
		IntStream.range(0, puzzle.length()).map(puzzle::charAt).forEach(c -> {
			if (c >= '0' && c <= '9') {
				final var point = new Point(x.get(), y.get());
				getCellAt(point).setValue((char) c);
			}

			x.getAndUpdate(i -> {
				if (++i >= size.width()) {
					y.getAndIncrement();

					return 0;
				}

				return i;
			});
		});
	}

	Dimension getSize();

	default List<Cell> getEmptyCells() {
		return getCells()
			.stream()
			.filter(Cell::isEmpty)
			.collect(Collectors.toList());
	}

	ImmutableCollection<Cell> getCells();

	Cell getCellAt(Point point);

}
