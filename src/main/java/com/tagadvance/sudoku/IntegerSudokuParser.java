package com.tagadvance.sudoku;

import com.tagadvance.geometry.Point;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class IntegerSudokuParser implements SudokuParser<Integer> {

	@Override
	public void populateSudokuFromString(final Grid<Integer> grid, final String puzzle) {
		final var size = grid.getSize();

		final var x = new AtomicInteger();
		final var y = new AtomicInteger();
		IntStream.range(0, puzzle.length()).map(puzzle::charAt).forEach(c -> {
			final var point = new Point(x.get(), y.get());
			final var cell = grid.getCellAt(point);
			if (c >= '0' && c <= '9') {
				cell.setValue(c - '0');
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

}
