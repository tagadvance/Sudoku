package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkNotNull;

import com.tagadvance.geometry.Point;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimpleSudokuSolver implements SudokuSolver {

	public SimpleSudokuSolver() {
		super();
	}

	@Override
	public  Grid solve(final Sudoku sudoku, final Grid grid) {
		checkNotNull(sudoku, "sudoku must not be null");
		checkNotNull(grid, "grid must not be null");

		return new InternalSudokuSolver(sudoku, grid.copy()).solve();
	}

	private static class InternalSudokuSolver {

		private final Sudoku sudoku;
		private final Grid alphaGrid;

		public InternalSudokuSolver(final Sudoku sudoku, final Grid grid) {
			super();
			this.sudoku = sudoku;
			this.alphaGrid = grid;
		}

		public Grid solve() {
			if (!sudoku.isValid(alphaGrid)) {
				// FIXME: throw exception
				return null;
			}

			final var grid = solve(alphaGrid);
			if (grid == null) {
				// FIXME: throw exception?
			}

			return grid;
		}

		private Grid solve(final Grid grid) {
			final var points = grid.getEmptyPoints();
			prioritize(grid, points);

			final var point = points.remove(0);
			final var cell = grid.getCellAt(point);
			for (final char value : sudoku.getPotentialValues(grid, point)) {
				cell.setValue(value);
				if (sudoku.isSolved(grid)) {
					return grid;
				} else if (points.isEmpty()) {
					return null;
				}

				final var result = solve(grid);
				if (result != null) {
					return result;
				}
			}

			cell.setValue(Cell.EMPTY);

			return null;
		}

		private void prioritize(final Grid grid, final List<Point> emptyPoints) {
			final Map<Point, Integer> counts = emptyPoints.stream()
				.collect(Collectors.toMap(Function.identity(),
					point -> sudoku.getPotentialValues(grid, point).size()));

			// sort from least to greatest
			emptyPoints.sort(Comparator.comparing(counts::get));
		}

	}

}
