package com.tagadvance.sudoku;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableSet;
import com.tagadvance.geometry.Dimension;
import com.tagadvance.geometry.ImmutableDimension;

public class Main {

	public static void main(String[] args)
			throws UnsolvableException, IOException, InterruptedException {
		List<Integer> range = IntStream.rangeClosed(1, 9).boxed().collect(Collectors.toList());
		ImmutableSet<Integer> values = ImmutableSet.copyOf(range);

		int width = 9, height = 9;
		ImmutableDimension size = new Dimension(width, height);
		ImmutableSet<Integer> emptyValues = ImmutableSet.of(0);
		CellFactory<Integer> cellFactory = new EmptyCellFactory<>(emptyValues);
		Grid<Integer> grid = new FixedSizeGrid<>(size, cellFactory);
		ScopeFactory scopeFactory = new SquareRootScopeFactory();
		ImmutableSet<Scope<Integer>> scopes = scopeFactory.createScopes(grid);
		Sudoku<Integer> sudoku = new CompositeSudoku<>(values, scopes);

		SudokuParser<Integer> parser = new IntegerSudokuParser();
		String puzzle =
				"?????64?9?3?2???1???6?7?????7???2??5?4?????2?9??6???3?????9?1???9???3?7?3?54?????";
		parser.populateSudokuFromString(grid, puzzle);
		System.out.println(grid);
		System.out.println();
		System.out.println();

		SudokuSolver solver = new SimpleSudokuSolver();
		Grid<Integer> result = solver.solve(sudoku, grid);
		System.out.println(result);
		System.out.println("**********");
	}

}
