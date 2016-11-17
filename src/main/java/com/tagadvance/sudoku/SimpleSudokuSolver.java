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
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.ImmutableSet;
import com.tagadvance.geometry.ImmutableDimension;
import com.tagadvance.geometry.ImmutablePoint;
import com.tagadvance.geometry.Point;

public class SimpleSudokuSolver implements SudokuSolver {

	public SimpleSudokuSolver() {
		super();
	}

	@Override
	public <V> Grid<V> solve(Sudoku<V> sudoku, Grid<V> grid) throws UnsolvableException {
		checkNotNull(sudoku, "sudoku must not be null");
		checkNotNull(grid, "grid must not be null");

		InternalSudokuSolver<V> solver = new InternalSudokuSolver<>(sudoku, grid);
		Solution<V> solution = solver.solve();
		System.out.println(solution);
		if (solution.grid == null) {
			throw new UnsolvableException();
		}
		return solution.grid;
	}

	private class InternalSudokuSolver<V> {

		private final Sudoku<V> sudoku;
		private final Grid<V> alphaGrid;

		private final Solution<V> solution;

		public InternalSudokuSolver(Sudoku<V> sudoku, Grid<V> grid) {
			super();
			this.sudoku = sudoku;
			this.alphaGrid = grid.copy();

			this.solution = new Solution<V>();
		}

		public Solution<V> solve() throws UnsolvableException {
			solution.start();

			if (!sudoku.isValid(alphaGrid)) {
				throw new UnsolvableException("sudoku is not in a valid state");
			}

			solve(alphaGrid);

			return solution;
		}

		private Grid<V> solve(Grid<V> grid) {
			if (sudoku.isSolved(grid)) {
				return grid;
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
					if (solution.grid == null) {
						solution.grid = result;
						solution.stop();
					}
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

	private class Solution<V> {

		private AtomicInteger iterations;
		private long start;
		private long stop;

		private Grid<V> grid;

		public Solution() {
			super();
			this.iterations = new AtomicInteger();
			this.start = System.currentTimeMillis();
		}

		public void start() {
			this.start = System.currentTimeMillis();
		}

		public void stop() {
			this.stop = System.currentTimeMillis();
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("iterations = " + iterations);
			if (start < stop) {
				sb.append("\r\n"); // TODO
				double seconds = calculateElapsedSeconds();
				sb.append(seconds + " seconds");
			}
			return sb.toString();
		}

		private double calculateElapsedSeconds() {
			return (stop - start) / 1000d;
		}

	}

}
