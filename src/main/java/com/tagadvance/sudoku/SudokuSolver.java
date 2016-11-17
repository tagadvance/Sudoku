package com.tagadvance.sudoku;

public interface SudokuSolver {

	public <V> Solution<V> solve(Sudoku<V> sudoku, Grid<V> grid);

}
