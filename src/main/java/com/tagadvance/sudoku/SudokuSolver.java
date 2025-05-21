package com.tagadvance.sudoku;

public interface SudokuSolver {

	<V> Grid<V> solve(Sudoku<V> sudoku, Grid<V> grid);

}
