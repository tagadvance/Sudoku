package com.tagadvance.sudoku;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
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
	public  Grid solve(final Sudoku sudoku, final Grid grid) {
		requireNonNull(sudoku, "sudoku must not be null");
		requireNonNull(grid, "grid must not be null");

		final var solver = new SudokuSolverRecursiveTask(sudoku, grid);

		return ForkJoinPool.commonPool().invoke(solver);
	}

	private static class SudokuSolverRecursiveTask extends RecursiveTask<Grid> {

		private final Sudoku sudoku;
		private final Grid grid;

		public SudokuSolverRecursiveTask(final Sudoku sudoku, final Grid grid) {
			super();
			this.sudoku = sudoku;
			this.grid = grid;
		}

		@Override
		protected Grid compute() {
			return solve(grid);
		}

		private Grid solve(final Grid grid) {
			final var cells = grid.getEmptyCells();
			prioritize(grid, cells);

			final var cell = cells.remove(0);

			final var forks = new ArrayList<ForkJoinTask<Grid>>();
			for (final var value : sudoku.getPotentialValuesForCell(grid, cell)) {
				cell.setValue(value);
				if (sudoku.isSolved(grid)) {
					return grid;
				} else if (cells.isEmpty()) {
					return null;
				}

				final var fork = new SudokuSolverRecursiveTask(sudoku, grid.copy()).fork();
				forks.add(fork);
			}

			return forks.stream()
				.map(ForkJoinTask::join)
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(null);
		}

		private void prioritize(final Grid grid, final List<Cell> cells) {
			final var potentialValueCountByCell = cells.stream()
				.collect(Collectors.toMap(Function.identity(),
					c -> sudoku.getPotentialValuesForCell(grid, c).size()));

			// sort from least to greatest
			cells.sort(Comparator.comparing(potentialValueCountByCell::get));
		}

	}

}
