package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import com.tagadvance.geometry.Dimension;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * Builds the pieces of a sudoku. The alphabet decides the size -- an n x n sudoku has exactly n
 * symbols -- so there is no way to ask for a 9 x 9 grid with sixteen values.
 */
public class SudokuBuilder {

	/**
	 * '1' through '9'.
	 */
	public static final ImmutableSet<Character> CLASSIC_VALUES = IntStream.rangeClosed('1', '9')
		.mapToObj(i -> (Character) (char) i)
		.collect(ImmutableSet.toImmutableSet());

	private ImmutableSet<Character> values = CLASSIC_VALUES;
	private ScopeFactory scopeFactory = new SquareRootScopeFactory();

	private SudokuBuilder() {
		super();
	}

	public static SudokuBuilder newBuilder() {
		return new SudokuBuilder();
	}

	/**
	 * @param values the alphabet, one symbol per row, column and block
	 */
	public SudokuBuilder values(final Set<Character> values) {
		this.values = ImmutableSet.copyOf(checkNotNull(values, "values must not be null"));

		return this;
	}

	/**
	 * Convenience for {@link #values(Set)}: every character of the string is one symbol, so a 16 x
	 * 16 puzzle is {@code values("123456789ABCDEFG")}.
	 */
	public SudokuBuilder values(final String values) {
		checkNotNull(values, "values must not be null");

		return values(values.chars()
			.mapToObj(i -> (Character) (char) i)
			.collect(ImmutableSet.toImmutableSet()));
	}

	public SudokuBuilder scopeFactory(final ScopeFactory scopeFactory) {
		this.scopeFactory = checkNotNull(scopeFactory, "scopeFactory must not be null");

		return this;
	}

	public SudokuFactory createSudokuFactory() {
		checkArgument(!values.isEmpty(), "values must not be empty");
		checkArgument(!values.contains(Cell.EMPTY), "values must not contain the empty sentinel");

		return new DefaultSudokuFactory(values, scopeFactory);
	}

	public SudokuFactory createClassicSudokuFactory() {
		return values(CLASSIC_VALUES).createSudokuFactory();
	}

	public interface SudokuFactory {

		/**
		 * @return the alphabet these puzzles are written in, for {@link Grid#populate}
		 */
		ImmutableSet<Character> getValues();

		Grid createEmptyGrid();

		Sudoku createSudoku();

	}

	private record DefaultSudokuFactory(ImmutableSet<Character> values,
										ScopeFactory scopeFactory) implements SudokuFactory {

		@Override
		public ImmutableSet<Character> getValues() {
			return values;
		}

		@Override
		public Grid createEmptyGrid() {
			final int size = values.size();

			return new FixedSizeGrid(new Dimension(size, size));
		}

		@Override
		public Sudoku createSudoku() {
			final var grid = createEmptyGrid();
			final var scopes = scopeFactory.createScopes(grid);

			return new CompositeSudoku(values, scopes);
		}

	}

}
