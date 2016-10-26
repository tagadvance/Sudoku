package com.tagadvance.sudoku;

import java.io.IOException;

public class Main {

	public static void main(String[] args)
			throws UnsolvableException, IOException, InterruptedException {
		Sudoku sudoku = Sudoku.demo(new ClassicSudoku(),
				"?????64?9?3?2???1???6?7?????7???2??5?4?????2?9??6???3?????9?1???9???3?7?3?54?????");
		Sudoku result = sudoku.solve();
		System.out.println(result);
		System.out.println("**********");
	}

}