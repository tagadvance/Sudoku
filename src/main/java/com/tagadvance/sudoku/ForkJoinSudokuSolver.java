package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicBoolean;

import com.tagadvance.geometry.ImmutableDimension;

public class ForkJoinSudokuSolver implements SudokuSolver {

	private final ForkDepthCalculator calculator;

	public ForkJoinSudokuSolver(ForkDepthCalculator calculator) {
		super();
		this.calculator = calculator;
	}

	@Override
	public <V> Solution<V> solve(Sudoku<V> sudoku, Grid<V> grid) {
		checkNotNull(sudoku, "sudoku must not be null");
		checkNotNull(grid, "grid must not be null");

		ForkJoinPool pool = ForkJoinPool.commonPool();

		// TODO: defensive copy of grid

		AtomicSolution<V> solution = new AtomicSolution<>();
		int depth = 0;
		SudokuSolverRecursiveTask<V> solver = new SudokuSolverRecursiveTask<>(sudoku, grid,
				solution, depth, new AtomicBoolean());
		pool.invoke(solver);
		return solution;
	}

	public static interface ForkDepthCalculator {

		public <V> int calculateForkDepth(Grid<V> grid);

	}

	public static class SquareRootForkDepthCalculator implements ForkDepthCalculator {

		private static final int MINIMUM_FORK_DEPTH = 1;

		@Override
		public <V> int calculateForkDepth(Grid<V> grid) {
			ImmutableDimension size = grid.getSize();
			int width = size.getWidth(), height = size.getHeight();
			int max = Math.max(width, height);
			double cubicRoot = Math.sqrt(max);
			int floor = (int) Math.floor(cubicRoot);
			return Math.max(MINIMUM_FORK_DEPTH, floor);
		}

	}

	@SuppressWarnings("serial")
	private class SudokuSolverRecursiveTask<V> extends RecursiveTask<Void> {

		private final List<RecursiveTask<Void>> tasks = new ArrayList<>();

		private final Sudoku<V> sudoku;
		private final Grid<V> grid;
		private final AtomicSolution<V> solution;
		private int depth;
		private final AtomicBoolean interrupt;

		private int forkDepth;

		public SudokuSolverRecursiveTask(Sudoku<V> sudoku, Grid<V> grid, AtomicSolution<V> solution,
				int depth, AtomicBoolean interrupt) {
			super();
			this.sudoku = sudoku;
			this.grid = grid;
			this.solution = solution;
			this.depth = depth;
			this.interrupt = interrupt;

			forkDepth = calculator.calculateForkDepth(grid);
		}

		@Override
		protected Void compute() {
			Grid<V> result = solve(grid, depth);
			if (result != null) {
				UnsolvableException e = null;
				solution.setSolution(result, e);
				interrupt.set(true);
			}
			joinAll();

			return null;
		}

		private Grid<V> solve(Grid<V> grid, int depth) {
			// another task already found the solution
			if (interrupt.get()) {
				return null;
			}

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

				if (depth == forkDepth) {
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

			// TODO: is this necessary?
			cell.setValue(null);

			return null;
		}

		private void joinAll() {
			for (RecursiveTask<Void> task : tasks) {
				task.join();
			}
		}

		private Map<Cell<V>, Integer> prioritize(Grid<V> grid, List<Cell<V>> emptyCells) {
			final Map<Cell<V>, Integer> cells = new HashMap<>();
			for (Cell<V> cell : emptyCells) {
				Set<V> potentialValues = sudoku.getPotentialValuesForCell(grid, cell);
				int size = potentialValues.size();
				cells.put(cell, size);
			}
			// sort from least to greatest
			Collections.sort(emptyCells, new Comparator<Cell<V>>() {
				@Override
				public int compare(Cell<V> c1, Cell<V> c2) {
					int size1 = cells.get(c1);
					int size2 = cells.get(c2);
					return (size1 == size2 ? 0 : (size1 < size2 ? -1 : 1));
				}
			});
			return cells;
		}

	}

}
