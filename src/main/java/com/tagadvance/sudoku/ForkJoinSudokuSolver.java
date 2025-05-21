package com.tagadvance.sudoku;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ForkJoinSudokuSolver implements SudokuSolver {

	public ForkJoinSudokuSolver() {
		super();
	}

	@Override
	public <V> Solution<V> solve(final Sudoku<V> sudoku, final Grid<V> grid) {
		requireNonNull(sudoku, "sudoku must not be null");
		requireNonNull(grid, "grid must not be null");

		final var solver = new SudokuSolverRecursiveTask<>(sudoku, grid);
		final var result = ForkJoinPool.commonPool().invoke(solver);

		final var solution = new AtomicSolution<V>();
		solution.setSolution(result, null);

		return solution;
	}

	private static class SudokuSolverRecursiveTask<V> extends RecursiveTask<Grid<V>> {

		private final Sudoku<V> sudoku;
		private final Grid<V> grid;

		public SudokuSolverRecursiveTask(final Sudoku<V> sudoku, final Grid<V> grid) {
			super();
			this.sudoku = sudoku;
			this.grid = grid;
		}

		@Override
		protected Grid<V> compute() {
			return solve(grid);
		}

		private Grid<V> solve(final Grid<V> grid) {
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
			if (cells.isEmpty()) {
				return null;
			}
			prioritize(grid, cells);

			final var cell = cells.remove(0);

			return sudoku.getPotentialValuesForCell(grid, cell)
				.stream()
				.peek(cell::setValue)
				.map(value -> new SudokuSolverRecursiveTask<>(sudoku, grid.copy()))
				.map(ForkJoinTask::fork)
				.toList()
				.stream()
				.map(ForkJoinTask::join)
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(null);
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
