package com.tagadvance.sudoku;

public interface SudokuParser<V> {

	void populateSudokuFromString(Grid<V> sudoku, String s);

}
