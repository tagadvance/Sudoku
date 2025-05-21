package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
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
	public <V> Solution<V> solve(final Sudoku<V> sudoku, final Grid<V> grid) {
		checkNotNull(sudoku, "sudoku must not be null");
		checkNotNull(grid, "grid must not be null");

		return new InternalSudokuSolver<>(sudoku, grid).solve();
	}

	private static class InternalSudokuSolver<V> {

		private final Sudoku<V> sudoku;
		private final Grid<V> alphaGrid;

		private final AtomicSolution<V> solution;

		public InternalSudokuSolver(final Sudoku<V> sudoku, final Grid<V> grid) {
			super();
			this.sudoku = sudoku;
			this.alphaGrid = grid.copy();

			this.solution = new AtomicSolution<>();
		}

		public Solution<V> solve() {
			Grid<V> grid = solve(alphaGrid);
			if (grid != null) {
				solution.setSolution(grid, null);
			}

			return solution;
		}

		private Grid<V> solve(final Grid<V> grid) {
			if (!sudoku.isValid(grid)) {
				return null;
			}

			if (sudoku.isSolved(grid)) {
				return grid;
			}

			final var cells = new ArrayList<>(grid.getCells());
			cells.removeIf(cell -> !cell.isEmpty());
			prioritize(grid, cells);
			if (cells.isEmpty()) {
				return null;
			}

			solution.iterations.incrementAndGet();

			final var cell = cells.remove(0);
			final var potentialCellValues = sudoku.getPotentialValuesForCell(grid, cell);
			for (final V value : potentialCellValues) {
				cell.setValue(value);
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
