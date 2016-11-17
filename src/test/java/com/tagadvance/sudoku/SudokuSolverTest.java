package com.tagadvance.sudoku;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.ImmutableSet;
import com.google.common.testing.NullPointerTester;
import com.tagadvance.geometry.Dimension;
import com.tagadvance.geometry.ImmutableDimension;


@RunWith(value = Parameterized.class)
public class SudokuSolverTest {

	@Parameters
	public static Iterable<Object[]> createParameters() throws IOException {
		List<Integer> range = IntStream.rangeClosed(1, 9).boxed().collect(Collectors.toList());
		ImmutableSet<Integer> values = ImmutableSet.copyOf(range);

		int width = 9, height = 9;
		ImmutableDimension size = new Dimension(width, height);
		ImmutableSet<Integer> emptyValues = ImmutableSet.of(0);
		CellFactory<Integer> cellFactory = new EmptyCellFactory<>(emptyValues);

		ScopeFactory scopeFactory = new SquareRootScopeFactory();
		Grid<Integer> alphaGrid = new FixedSizeGrid<>(size, cellFactory);
		ImmutableSet<Scope<Integer>> scopes = scopeFactory.createScopes(alphaGrid);
		Sudoku<Integer> sudoku = new CompositeSudoku<>(values, scopes);

		SudokuParser<Integer> parser = new IntegerSudokuParser();

		List<Grid<Integer>> gridList = new ArrayList<>();
		String name = "/puzzles.txt";
		try (InputStream is = SudokuSolverTest.class.getResourceAsStream(name);
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader reader = new BufferedReader(isr)) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				}

				Grid<Integer> gridCopy = alphaGrid.copy();
				String puzzle =
						"?????64?9?3?2???1???6?7?????7???2??5?4?????2?9??6???3?????9?1???9???3?7?3?54?????";
				parser.populateSudokuFromString(gridCopy, puzzle);

				gridList.add(gridCopy);
			}
		}

		List<Object[]> list = new ArrayList<>();
		SudokuSolver[] solvers = {new SimpleSudokuSolver()/* , new FastSudokuSolver() */};
		for (SudokuSolver solver : solvers) {
			for (Grid<Integer> betaGrid : gridList) {
				list.add(new Object[] {solver, sudoku, betaGrid});
			}
		}
		return list;
	}

	private SudokuSolver solver;
	private Sudoku<Integer> sudoku;
	private Grid<Integer> grid;

	public SudokuSolverTest(SudokuSolver solver, Sudoku<Integer> sudoku, Grid<Integer> grid) {
		this.solver = solver;
		this.sudoku = sudoku;
		this.grid = grid;
	}

	@Test
	public void solveThrowsNPE() throws NoSuchMethodException, SecurityException {
		NullPointerTester tester = new NullPointerTester();
		Method method = SudokuSolver.class.getMethod("solve", Sudoku.class, Grid.class);
		tester.testMethod(solver, method);
	}

	@Test
	public void solveReturnsSolvedSudoku() throws IOException, UnsolvableException {
		Grid<Integer> grid = solver.solve(sudoku, this.grid);
		Assert.assertTrue(sudoku.isSolved(grid));
	}

}
