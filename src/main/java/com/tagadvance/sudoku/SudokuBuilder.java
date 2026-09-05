package com.tagadvance.sudoku;

import com.google.common.collect.ImmutableSet;
import com.tagadvance.geometry.Dimension;
import java.util.stream.IntStream;

/**
 * Factory dependencies are hard-coded until I can refactor this into a step builder.
 */
public class SudokuBuilder {

	private final ScopeFactory scopeFactory;

	private SudokuBuilder() {
		this.scopeFactory = new SquareRootScopeFactory();
	}

	public static SudokuBuilder newBuilder() {
		return new SudokuBuilder();
	}

	public SudokuFactory createClassicSudokuFactory() {
		return new ClassicSudokuFactory(scopeFactory);
	}

	public interface SudokuFactory {

		Grid createEmptyGrid();

		Sudoku createSudoku();

	}

	private static class ClassicSudokuFactory implements SudokuFactory {

		private static final int SIZE = 9;
		private static final char RANGE_START = '1', RANGE_END = '9';

		private static final ImmutableSet<Character> values = IntStream.rangeClosed(RANGE_START,
			RANGE_END).mapToObj(i -> (Character) (char) i).collect(ImmutableSet.toImmutableSet());

		private final ScopeFactory scopeFactory;

		public ClassicSudokuFactory(final ScopeFactory scopeFactory) {
			super();
			this.scopeFactory = scopeFactory;
		}

		@Override
		public Grid createEmptyGrid() {
			final var size = new Dimension(SIZE, SIZE);

			return new FixedSizeGrid(size);
		}

		@Override
		public Sudoku createSudoku() {
			final var grid = createEmptyGrid();
			final var scopes = scopeFactory.createScopes(grid);

			return new CompositeSudoku(values, scopes);
		}

	}

}
