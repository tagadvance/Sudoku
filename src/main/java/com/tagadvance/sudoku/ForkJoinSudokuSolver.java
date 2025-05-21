package com.tagadvance.sudoku;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ForkJoinSudokuSolver implements SudokuSolver {

	private final ForkDepthCalculator calculator;

	public ForkJoinSudokuSolver(final ForkDepthCalculator calculator) {
		super();
		this.calculator = requireNonNull(calculator, "calculator must not be null");
	}

	@Override
	public <V> Solution<V> solve(final Sudoku<V> sudoku, final Grid<V> grid) {
		requireNonNull(sudoku, "sudoku must not be null");
		requireNonNull(grid, "grid must not be null");

		final var pool = ForkJoinPool.commonPool();

		// TODO: defensive copy of grid

		final var solution = new AtomicSolution<V>();
		final int depth = 0;
		final var solver = new SudokuSolverRecursiveTask<>(sudoku, grid, solution, depth,
			new AtomicBoolean());
		pool.invoke(solver);

		return solution;
	}

	public interface ForkDepthCalculator {

		<V> int calculateForkDepth(Grid<V> grid);

	}

	public static class SquareRootForkDepthCalculator implements ForkDepthCalculator {

		private static final int MINIMUM_FORK_DEPTH = 1;

		@Override
		public <V> int calculateForkDepth(final Grid<V> grid) {
			final var size = grid.getSize();
			final int width = size.width(), height = size.height();
			final int max = Math.max(width, height);
			final double cubicRoot = Math.sqrt(max);
			final int floor = (int) Math.floor(cubicRoot);

			return Math.max(MINIMUM_FORK_DEPTH, floor);
		}

	}

	private class SudokuSolverRecursiveTask<V> extends RecursiveTask<Void> {

		private final List<RecursiveTask<Void>> tasks = new ArrayList<>();

		private final Sudoku<V> sudoku;
		private final Grid<V> grid;
		private final AtomicSolution<V> solution;
		private final int depth;
		private final AtomicBoolean interrupt;

		private final int forkDepth;

		public SudokuSolverRecursiveTask(final Sudoku<V> sudoku, final Grid<V> grid,
			final AtomicSolution<V> solution, int depth, AtomicBoolean interrupt) {
			super();
			this.sudoku = sudoku;
			this.grid = grid;
			this.solution = solution;
			this.depth = depth;
			this.interrupt = interrupt;

			this.forkDepth = calculator.calculateForkDepth(grid);
		}

		@Override
		protected Void compute() {
			Grid<V> result = solve(grid, depth);
			if (result != null) {
				solution.setSolution(result, null);
				interrupt.set(true);
			}
			joinAll();

			return null;
		}

		private Grid<V> solve(final Grid<V> grid, int depth) {
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

			final var cells = grid.getCells()
				.stream()
				.filter(Cell::isEmpty)
				.collect(Collectors.toList());
			prioritize(grid, cells);
			if (cells.isEmpty()) {
				return null;
			}

			solution.iterations.incrementAndGet();

			final var cell = cells.remove(0);
			final var potentialCellValues = sudoku.getPotentialValuesForCell(grid, cell);
			for (final V value : potentialCellValues) {
				cell.setValue(value);

				if (depth == forkDepth) {
					final var clone = grid.copy();
					final var task = new SudokuSolverRecursiveTask<>(sudoku, clone, solution,
						depth++, interrupt);
					tasks.add(task);
					task.fork();
				} else {
					final var result = solve(grid, depth++);
					if (result != null) {
						return result;
					}
				}
			}

			cell.setValue(null);

			return null;
		}

		private void joinAll() {
			tasks.forEach(ForkJoinTask::join);
		}

		private void prioritize(final Grid<V> grid, final List<Cell<V>> cells) {
			final var potentialValueCountByCell = cells.stream()
				.collect(Collectors.toMap(Function.identity(),
					c -> sudoku.getPotentialValuesForCell(grid, c).size()));

			// sort from least to greatest
			cells.sort(Comparator.comparing(potentialValueCountByCell::get));
		}

	}

}
