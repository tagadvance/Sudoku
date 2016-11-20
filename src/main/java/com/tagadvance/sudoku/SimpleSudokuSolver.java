package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimpleSudokuSolver implements SudokuSolver {

	public SimpleSudokuSolver() {
		super();
	}

	@Override
	public <V> Solution<V> solve(Sudoku<V> sudoku, Grid<V> grid) {
		checkNotNull(sudoku, "sudoku must not be null");
		checkNotNull(grid, "grid must not be null");

		InternalSudokuSolver<V> solver = new InternalSudokuSolver<>(sudoku, grid);
		return solver.solve();
	}

	private class InternalSudokuSolver<V> {

		private final Sudoku<V> sudoku;
		private final Grid<V> alphaGrid;

		private final AtomicSolution<V> solution;

		public InternalSudokuSolver(Sudoku<V> sudoku, Grid<V> grid) {
			super();
			this.sudoku = sudoku;
			this.alphaGrid = grid.copy();

			this.solution = new AtomicSolution<V>();
		}

		public Solution<V> solve() {
			Grid<V> grid = solve(alphaGrid);
			if (grid != null) {
				UnsolvableException e = null;
				solution.setSolution(grid, e);
			}

			return solution;
		}

		private Grid<V> solve(Grid<V> grid) {
			if (!sudoku.isValid(grid)) {
				return null;
			}
			
			if (sudoku.isSolved(grid)) {
				return grid;
			}

			List<Cell<V>> cells = new ArrayList<>(grid.getCells());
			cells.removeIf(cell -> !cell.isEmpty());
			prioritize(grid, cells);
			if (cells.isEmpty()) {
				return null;
			}

			solution.iterations.incrementAndGet();

			Cell<V> cell = cells.remove(0);
			Set<V> potentialCellValues = sudoku.getPotentialValuesForCell(grid, cell);
			for (V value : potentialCellValues) {
				cell.setValue(value);
				Grid<V> result = solve(grid);
				if (result != null) {
					return result;
				}
			}

			cell.setValue(null);

			return null;
		}

		private Map<Cell<V>, Integer> prioritize(Grid<V> grid, List<Cell<V>> emptyCells) {
			final Map<Cell<V>, Integer> cells = new HashMap<>();
			for (Cell<V> cell : emptyCells) {
				Set<V> potentialValues = sudoku.getPotentialValuesForCell(grid, cell);
				int size = potentialValues.size();
				cells.put(cell, size);
			}
			// sort from least to greatest
			Collections.sort(emptyCells, (cell1, cell2) -> {
				return cells.get(cell1).compareTo(cells.get(cell2));
			});
			return cells;
		}

	}

}
