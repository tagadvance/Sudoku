package com.tagadvance.sudoku;

import static java.util.function.Predicate.not;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.CharStreams;
import com.tagadvance.geometry.Dimension;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class SudokuSolverTest {

	static Stream<Object[]> createParameters() throws IOException {
		final var values = IntStream.rangeClosed('1', '9')
			.mapToObj(i -> (Character)(char) i)
			.collect(ImmutableSet.toImmutableSet());

		final int width = 9, height = 9;
		final var size = new Dimension(width, height);
		final var grid = new FixedSizeGrid(size);
		var puzzles = readPuzzles(grid).toList();

		final var scopeFactory = new SquareRootScopeFactory();
		final var scopes = scopeFactory.createScopes(grid);
		final var sudoku = new CompositeSudoku(values, scopes);

		return Stream.of(new SimpleSudokuSolver()/*, new ForkJoinSudokuSolver()*/)
			.flatMap(
				solver -> puzzles.stream().map(puzzle -> new Object[]{solver, sudoku, puzzle}));
	}

	private static Stream<Grid> readPuzzles(final Grid grid) throws IOException {
		try (final var is = SudokuSolverTest.class.getResourceAsStream("/puzzles.txt")) {
			if (is == null) {
				return Stream.empty();
			}

			try (final var in = new InputStreamReader(is)) {
				return CharStreams.readLines(in)
					.stream()
					.map(String::trim)
					.filter(not(line -> line.isEmpty() || line.startsWith("#")))
					.map(line -> {
						final var puzzleGrid = grid.copy();
						puzzleGrid.populate(line);

						return puzzleGrid;
					});
			}
		}
	}

	@ParameterizedTest
	@MethodSource("createParameters")
	// TODO: performance test both
	void solveReturnsSolvedSudoku(SudokuSolver solver, Sudoku sudoku, Grid grid) {
		final var solution = solver.solve(sudoku, grid);

		assertTrue(sudoku.isSolved(solution));
	}

}
