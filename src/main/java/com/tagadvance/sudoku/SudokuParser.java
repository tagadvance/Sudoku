package com.tagadvance.sudoku;

public interface SudokuParser<V> {

	public void populateSudokuFromString(Grid<V> sudoku, String s);

}
