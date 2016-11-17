package com.tagadvance.sudoku;

public interface SudokuSolver {

	public <V> Grid<V> solve(Sudoku<V> sudoku, Grid<V> grid) throws UnsolvableException;

}
