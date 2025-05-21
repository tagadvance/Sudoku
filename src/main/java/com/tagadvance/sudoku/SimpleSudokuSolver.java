package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkNotNull;

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
	public <V> Grid<V> solve(final Sudoku<V> sudoku, final Grid<V> grid) {
		checkNotNull(sudoku, "sudoku must not be null");
		checkNotNull(grid, "grid must not be null");

		return new InternalSudokuSolver<>(sudoku, grid.copy()).solve();
	}

	private static class InternalSudokuSolver<V> {

		private final Sudoku<V> sudoku;
		private final Grid<V> alphaGrid;

		public InternalSudokuSolver(final Sudoku<V> sudoku, final Grid<V> grid) {
			super();
			this.sudoku = sudoku;
			this.alphaGrid = grid;
		}

		public Grid<V> solve() {
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

		private Grid<V> solve(final Grid<V> grid) {
			final var cells = grid.getEmptyCells();
			prioritize(grid, cells);

			final var cell = cells.remove(0);
			final var potentialCellValues = sudoku.getPotentialValuesForCell(grid, cell);
			for (final V value : potentialCellValues) {
				cell.setValue(value);
				if (sudoku.isSolved(grid)) {
					return grid;
				} else if (cells.isEmpty()) {
					return null;
				}

				final var result = solve(grid);
				if (result != null) {
					return result;
				}
			}

			cell.setValue(null);

			return null;
		}

		private void prioritize(final Grid<V> grid, final List<Cell<V>> emptyCells) {
			final Map<Cell<V>, Integer> cells = emptyCells.stream()
				.collect(Collectors.toMap(Function.identity(),
					cell -> sudoku.getPotentialValuesForCell(grid, cell).size()));

			// sort from least to greatest
			emptyCells.sort(Comparator.comparing(cells::get));
		}

	}

}
