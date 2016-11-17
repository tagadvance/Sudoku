package com.tagadvance.sudoku;

import org.junit.Assert;
import org.junit.Test;

import com.tagadvance.sudoku.SudokuBuilder.SudokuFactory;

public class SudokuBuilderTest {

	@Test
	public void newBuilderReturnsNewInstance() throws NoSuchMethodException, SecurityException {
		SudokuBuilder builder = SudokuBuilder.newBuilder();
		Assert.assertNotNull(builder);
	}

	@Test
	public void createClassicSudokuFactoryReturnsNewInstance()
			throws NoSuchMethodException, SecurityException {
		SudokuBuilder builder = SudokuBuilder.newBuilder();
		SudokuFactory<Integer> factory = builder.createClassicSudokuFactory();
		Assert.assertNotNull(factory);
	}

	@Test
	public void createEmptyGridReturnsNewInstance()
			throws NoSuchMethodException, SecurityException {
		SudokuBuilder builder = SudokuBuilder.newBuilder();
		SudokuFactory<Integer> factory = builder.createClassicSudokuFactory();
		Grid<Integer> grid = factory.createEmptyGrid();
		Assert.assertNotNull(grid);
	}

	@Test
	public void createSudokuReturnsNewInstance() throws NoSuchMethodException, SecurityException {
		SudokuBuilder builder = SudokuBuilder.newBuilder();
		SudokuFactory<Integer> factory = builder.createClassicSudokuFactory();
		Sudoku<Integer> sudoku = factory.createSudoku();
		Assert.assertNotNull(sudoku);
	}

}
