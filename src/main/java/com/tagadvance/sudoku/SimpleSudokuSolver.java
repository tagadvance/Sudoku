package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.tagadvance.geometry.ImmutableDimension;
import com.tagadvance.geometry.ImmutablePoint;
import com.tagadvance.geometry.Point;

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
			if (!sudoku.isValid(alphaGrid)) {
				Grid<V> grid = null;
				UnsolvableException e = new UnsolvableException("sudoku is not in a valid state");
				solution.setSolution(grid, e);
				return solution;
			}

			Grid<V> grid = solve(alphaGrid);
			if (grid != null) {
				UnsolvableException e = null;
				solution.setSolution(grid, e);
			}

			return solution;
		}

		private Grid<V> solve(Grid<V> grid) {
			if (sudoku.isSolved(grid)) {
				return grid;
			}
			
			if (!sudoku.isValid(grid)) {
				return null;
			}

			List<ImmutablePoint> cells = getPrioritizedCellList(grid);
			if (cells.isEmpty()) {
				return null;
			}

			solution.iterations.incrementAndGet();

			ImmutablePoint point = cells.remove(0);
			Set<V> potentialCellValues = getPotentialValuesForCellAt(grid, point);
			Cell<V> cell = grid.getCellAt(point);
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

		private List<ImmutablePoint> getPrioritizedCellList(Grid<V> grid) {
			List<ImmutablePoint> emptyCells = getEmptyCells(grid);
			prioritize(grid, emptyCells);
			return emptyCells;
		}

		private List<ImmutablePoint> getEmptyCells(Grid<V> grid) {
			ImmutableDimension size = grid.getSize();
			List<ImmutablePoint> list = new ArrayList<>();
			for (int y = 0; y < size.getHeight(); y++) {
				for (int x = 0; x < size.getWidth(); x++) {
					ImmutablePoint point = new Point(x, y);
					Cell<V> cell = grid.getCellAt(point);
					if (cell.isEmpty()) {
						list.add(point);
					}
				}
			}
			return list;
		}

		private Map<ImmutablePoint, Integer> prioritize(Grid<V> grid,
				List<ImmutablePoint> emptyCells) {
			final Map<ImmutablePoint, Integer> cells = new HashMap<ImmutablePoint, Integer>();
			for (ImmutablePoint point : emptyCells) {
				Set<V> potentialValues = getPotentialValuesForCellAt(grid, point);
				int size = potentialValues.size();
				cells.put(point, size);
			}
			// sort from least to greatest
			Collections.sort(emptyCells, new Comparator<ImmutablePoint>() {
				@Override
				public int compare(ImmutablePoint p1, ImmutablePoint p2) {
					int size1 = cells.get(p1);
					int size2 = cells.get(p2);
					return (size1 == size2 ? 0 : (size1 < size2 ? -1 : 1));
				}
			});
			return cells;
		}

		/**
		 * Iterate over each of the cell's scopes and get the unused potential values common to each
		 * scope, i.e. weed out any values that can't possibly be correct.
		 */
		private Set<V> getPotentialValuesForCellAt(Grid<V> grid, ImmutablePoint point) {
			ImmutableSet<V> values = sudoku.getValues();
			Set<V> potentialValues = new HashSet<>(values);

			// System.out.println(potentialValues);
			// System.out.println("values: " + Joiner.on(' ').join(potentialValues));

			Set<Scope<V>> scopeSet = sudoku.getScopesForPoint(point);
			for (Scope<V> scope : scopeSet) {
				Set<V> usedValues = scope.getUsedValues(grid);
				// System.out.println("removing: " + Joiner.on(' ').join(usedValues));
				potentialValues.removeAll(usedValues);
			}
			// System.out.println("values: " + Joiner.on(' ').join(potentialValues));

			return potentialValues;
		}

	}

}
