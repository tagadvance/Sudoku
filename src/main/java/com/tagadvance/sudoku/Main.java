package com.tagadvance.sudoku;

import java.io.IOException;

public class Main {

	public static void main(String[] args)
			throws UnsolvableException, IOException, InterruptedException {
		SudokuParser parser = new SimpleSudokuParser();
		Sudoku sudoku = new ClassicSudoku();
		String puzzle = "?????64?9?3?2???1???6?7?????7???2??5?4?????2?9??6???3?????9?1???9???3?7?3?54?????";
		parser.populateSudokuFromString(sudoku, puzzle);
		SudokuSolver solver = new SimpleSudokuSolver();
		Sudoku result = solver.solve(sudoku);
		System.out.println(result);
		System.out.println("**********");
	}

}
