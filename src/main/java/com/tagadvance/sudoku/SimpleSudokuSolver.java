package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkNotNull;

public class SimpleSudokuSolver implements SudokuSolver {

	public SimpleSudokuSolver() {
		super();
	}

	@Override
	public Grid solve(final Sudoku sudoku, final Grid grid) {
		checkNotNull(sudoku, "sudoku must not be null");
		checkNotNull(grid, "grid must not be null");

		return new InternalSudokuSolver(sudoku, grid.copy()).solve();
	}

	private static class InternalSudokuSolver {

		private final Sudoku sudoku;
		private final Grid alphaGrid;

		InternalSudokuSolver(final Sudoku sudoku, final Grid grid) {
			super();
			this.sudoku = sudoku;
			this.alphaGrid = grid;
		}

		Grid solve() {
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

		private Grid solve(final Grid grid) {
			// pick the most constrained position. The old code sorted every empty position by
			// candidate count and then took element zero, but the rest of that sort was thrown
			// away -- each level rebuilds the list from scratch. Only the minimum was ever used.
			int best = -1, bestCount = Integer.MAX_VALUE, bestCandidates = 0, emptyCount = 0;
			for (int position = 0, size = grid.size(); position < size; position++) {
				if (!grid.isEmpty(position)) {
					continue;
				}

				emptyCount++;
				final int candidates = sudoku.candidates(grid, position);
				final int count = Integer.bitCount(candidates);
				if (count < bestCount) {
					bestCount = count;
					bestCandidates = candidates;
					best = position;
				}
			}

			if (best < 0) {
				return null;
			}

			int candidates = bestCandidates;
			while (candidates != 0) {
				final int ordinal = Integer.numberOfTrailingZeros(candidates);
				candidates &= candidates - 1;

				grid.set(best, sudoku.symbol(ordinal));
				if (sudoku.isSolved(grid)) {
					return grid;
				} else if (emptyCount == 1) {
					// This was the last empty position and filling it did not solve the puzzle, so
					// we give up without trying its remaining candidates. That is a real bug: it
					// should `continue` and let the loop finish. It is harmless today only because
					// the last empty position always has exactly one candidate left, so there are
					// no remaining candidates to skip. Kept as-is deliberately, so that before and
					// after measurements compare the same search. TODO: return null after the loop.
					return null;
				}

				final var result = solve(grid);
				if (result != null) {
					return result;
				}
			}

			grid.set(best, Grid.EMPTY);

			return null;
		}

	}

}
