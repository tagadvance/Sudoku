package com.tagadvance.sudoku;

import java.io.IOException;

public class Main {

	public static void main(String[] args)
			throws UnsolvableException, IOException, InterruptedException {
		SudokuSolver solver = new SimpleSudokuSolver();
		Sudoku sudoku = demo(new ClassicSudoku(),
				"?????64?9?3?2???1???6?7?????7???2??5?4?????2?9??6???3?????9?1???9???3?7?3?54?????");
		Sudoku result = solver.solve(sudoku);
		System.out.println(result);
		System.out.println("**********");
	}
	
	public static Sudoku demo(AbstractSudoku sudoku, String puzzle) {
		int x = 0, y = 0;
		for (int i = 0; i < puzzle.length(); i++) {
			String value = puzzle.substring(i, i + 1);
			sudoku.setCellValue(x, y, value);
			if (++x >= sudoku.width) {
				y++;
				x = 0;
			}
		}
		return sudoku;
	}

}
