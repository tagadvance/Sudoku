package com.tagadvance.sudoku;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.testing.NullPointerTester;


@RunWith(value = Parameterized.class)
public class SudokuSolverTest {

	@Parameters
	public static Iterable<Object[]> createParameters() throws IOException {
		List<Object[]> list = new ArrayList<>();

		SudokuSolver[] solvers = {new SimpleSudokuSolver()/* , new FastSudokuSolver() */};

		List<Sudoku> sudokuList = new ArrayList<>();

		SudokuParser parser = new SimpleSudokuParser();
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

				System.out.println(line);
				Sudoku sudoku = new ClassicSudoku();
				parser.populateSudokuFromString(sudoku, line);
				sudokuList.add(sudoku);
			}
		}

		for (SudokuSolver solver : solvers) {
			for (Sudoku sudoku : sudokuList) {
				list.add(new Object[] {solver, sudoku});
			}
		}

		return list;
	}

	private SudokuSolver solver;
	private Sudoku sudoku;

	public SudokuSolverTest(SudokuSolver solver, Sudoku sudoku) {
		this.solver = solver;
		this.sudoku = sudoku;
	}

	@Test
	public void solveThrowsNPE() throws NoSuchMethodException, SecurityException {
		NullPointerTester tester = new NullPointerTester();
		Method method = SudokuSolver.class.getMethod("solve", Sudoku.class);
		tester.testMethod(solver, method);
	}

	@Test
	public void simpleSolveReturnsSolvedSudoku() throws IOException, UnsolvableException {
		Sudoku solvedSudoku = solver.solve(sudoku);
		Assert.assertTrue(solvedSudoku.isSolved());
	}

}
