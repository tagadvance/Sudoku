package com.tagadvance.sudoku;

import static java.util.function.Predicate.not;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.CharStreams;
import com.google.common.testing.NullPointerTester;
import com.tagadvance.geometry.Dimension;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class SudokuSolverTest {

	static Stream<Object[]> createParameters() throws IOException {
		final var values = IntStream.rangeClosed(1, 9)
			.boxed()
			.collect(ImmutableSet.toImmutableSet());

		final int width = 9, height = 9;
		final var size = new Dimension(width, height);
		final var grid = new FixedSizeGrid<Integer>(size);
		var puzzles = readPuzzles(grid).toList();

		final var scopeFactory = new SquareRootScopeFactory();
		final var scopes = scopeFactory.createScopes(grid);
		final var sudoku = new CompositeSudoku<>(values, scopes);

		return Stream.of(new SimpleSudokuSolver(), new ForkJoinSudokuSolver())
			.flatMap(
				solver -> puzzles.stream().map(puzzle -> new Object[]{solver, sudoku, puzzle}));
	}

	private static Stream<Grid<Integer>> readPuzzles(final Grid<Integer> grid) throws IOException {
		final var parser = new IntegerSudokuParser();

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
						Grid<Integer> puzzleGrid = grid.copy();
						parser.populateSudokuFromString(puzzleGrid, line);

						return puzzleGrid;
					});
			}
		}
	}

	@ParameterizedTest
	@MethodSource("createParameters")
	void solveThrowsNPE(SudokuSolver solver) throws NoSuchMethodException, SecurityException {
		NullPointerTester tester = new NullPointerTester();
		Method method = SudokuSolver.class.getMethod("solve", Sudoku.class, Grid.class);
		tester.testMethod(solver, method);
	}

	@ParameterizedTest
	@MethodSource("createParameters")
	void solveReturnsSolvedSudoku(SudokuSolver solver, Sudoku<Integer> sudoku, Grid<Integer> grid)
		throws UnsolvableException {
		Solution<Integer> solution = solver.solve(sudoku, grid);
		Grid<Integer> g = solution.getSolution();
		assertTrue(sudoku.isSolved(g));
	}

}
