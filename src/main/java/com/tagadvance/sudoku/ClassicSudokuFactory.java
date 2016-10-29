package com.tagadvance.sudoku;

public class ClassicSudokuFactory implements SudokuFactory {

	@Override
	public Sudoku createEmptySudoku() {
		return new ClassicSudoku();
	}

}
