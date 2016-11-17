package com.tagadvance.sudoku;

import java.io.IOException;

import com.tagadvance.sudoku.SudokuBuilder.SudokuFactory;

public class Main {

	public static void main(String[] args)
			throws UnsolvableException, IOException, InterruptedException {
		SudokuFactory<Integer> factory = SudokuBuilder.newBuilder().createClassicSudokuFactory();
		Sudoku<Integer> sudoku = factory.createSudoku();
		Grid<Integer> grid = factory.createEmptyGrid();

		SudokuParser<Integer> parser = new IntegerSudokuParser();
		String puzzle =
				"?????64?9?3?2???1???6?7?????7???2??5?4?????2?9??6???3?????9?1???9???3?7?3?54?????";
		parser.populateSudokuFromString(grid, puzzle);
		System.out.println(grid);
		System.out.println();

		SudokuSolver solver = new SimpleSudokuSolver();
		Grid<Integer> result = solver.solve(sudoku, grid);
		System.out.println(result);
		System.out.println("**********");
	}

}
