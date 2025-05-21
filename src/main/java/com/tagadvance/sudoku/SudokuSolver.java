package com.tagadvance.sudoku;

public interface SudokuSolver {

	<V> Solution<V> solve(Sudoku<V> sudoku, Grid<V> grid);

}
