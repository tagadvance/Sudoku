package com.tagadvance.sudoku;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableSet;
import com.tagadvance.geometry.Dimension;
import com.tagadvance.geometry.ImmutableDimension;

public class SudokuBuilder {
	
	private final CellFactory<Integer> cellFactory;
	private final ScopeFactory scopeFactory;

	private SudokuBuilder() {
		this.cellFactory = new EmptyCellFactory<>();
		this.scopeFactory = new SquareRootScopeFactory();
	}

	public SudokuBuilder newBuilder() {
		return new SudokuBuilder();
	}
	
	public createClassicSudokuFactory() {
		return new ClassicSudokuFactory(cellFactory, scopeFactory);
	}

	private static interface SudokuFactory<V> {

		public Grid<V> createEmptyGrid();

		public Sudoku<V> createSudoku();

	}

	private static class ClassicSudokuFactory implements SudokuFactory<Integer> {

		private static final int SIZE = 9, RANGE_START = 1, RANGE_END = 9;

		private static final ImmutableSet<Integer> values;
		static {
			List<Integer> range = IntStream.rangeClosed(RANGE_START, RANGE_END).boxed()
					.collect(Collectors.toList());
			values = ImmutableSet.copyOf(range);
		}

		private final CellFactory<Integer> cellFactory;
		private final ScopeFactory scopeFactory;

		public ClassicSudokuFactory(CellFactory<Integer> cellFactory, ScopeFactory scopeFactory) {
			super();
			this.cellFactory = cellFactory;
			this.scopeFactory = scopeFactory;
		}

		@Override
		public Grid<Integer> createEmptyGrid() {
			ImmutableDimension size = new Dimension(SIZE, SIZE);
			return new FixedSizeGrid<Integer>(size, cellFactory);
		}

		@Override
		public Sudoku<Integer> createSudoku() {
			Grid<Integer> grid = createEmptyGrid();
			ImmutableSet<Scope<Integer>> scopes = scopeFactory.createScopes(grid);
			return new CompositeSudoku<>(values, scopes);
		}

	}

}
