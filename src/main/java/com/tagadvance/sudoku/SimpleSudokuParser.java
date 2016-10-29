package com.tagadvance.sudoku;

import java.awt.Dimension;

public class SimpleSudokuParser implements SudokuParser {

	@Override
	public void populateSudokuFromString(Sudoku sudoku, String puzzle) {
		sudoku.clear();
		Dimension size = sudoku.getSize();

		int x = 0, y = 0;
		for (int i = 0; i < puzzle.length(); i++) {
			String value = puzzle.substring(i, i + 1);
			sudoku.setCellValue(x, y, value);
			if (++x >= size.width) {
				y++;
				x = 0;
			}
		}
	}

}
