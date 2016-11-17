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
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.ImmutableSet;
import com.tagadvance.geometry.ImmutableDimension;
import com.tagadvance.geometry.ImmutablePoint;
import com.tagadvance.geometry.Point;

public class ForkJoinSudokuSolver implements SudokuSolver {

	public ForkJoinSudokuSolver() {
		super();
	}

	@Override
	public <V> Grid<V> solve(Sudoku<V> sudoku, Grid<V> grid) throws UnsolvableException {
		checkNotNull(sudoku, "sudoku must not be null");
		checkNotNull(grid, "grid must not be null");

		ForkJoinPool pool = ForkJoinPool.commonPool();

		// TODO: copy grid

		Solution<V> solution = new Solution<>();
		solution.start();
		int depth = 0;
		SudokuSolverRecursiveTask<V> solver =
				new SudokuSolverRecursiveTask<>(sudoku, grid, solution, depth, new AtomicBoolean());
		pool.invoke(solver);
		solution.stop();

		System.out.println(solution);
		if (solution.grid == null) {
			throw new UnsolvableException();
		}
		return solution.grid;
	}

	@SuppressWarnings("serial")
	private class SudokuSolverRecursiveTask<V> extends RecursiveTask<Void> {

		private static final int FORK_DEPTH = 4;

		private final List<RecursiveTask<Void>> tasks = new ArrayList<>();

		private final Sudoku<V> sudoku;
		private final Grid<V> grid;
		private final Solution<V> solution;
		private int depth;
		private final AtomicBoolean interrupt;

		public SudokuSolverRecursiveTask(Sudoku<V> sudoku, Grid<V> grid, Solution<V> solution,
				int depth, AtomicBoolean interrupt) {
			super();
			this.sudoku = sudoku;
			this.grid = grid;
			this.solution = solution;
			this.depth = depth;
			this.interrupt = interrupt;
		}

		@Override
		protected Void compute() {
			if (sudoku.isValid(grid)) {
				Grid<V> result = solve(grid, depth);
				if (result != null) {
					solution.grid = grid;
					interrupt.set(true);
				}
				joinAll();
			} else {
				// throw new UnsolvableException("sudoku is not in a valid state");
			}

			return null;
		}

		private Grid<V> solve(Grid<V> grid, int depth) {
			// another task already found the solution
			if (interrupt.get()) {
				return null;
			}

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

				if (depth == FORK_DEPTH) {
					Grid<V> clone = grid.copy();
					SudokuSolverRecursiveTask<V> task = new SudokuSolverRecursiveTask<>(sudoku,
							clone, solution, depth++, interrupt);
					tasks.add(task);
					task.fork();
				} else {
					Grid<V> result = solve(grid, depth++);
					if (result != null) {
						return result;
					}
				}
			}

			cell.setValue(null);

			return null;
		}

		private void joinAll() {
			for (RecursiveTask<Void> task : tasks) {
				task.join();
			}
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
