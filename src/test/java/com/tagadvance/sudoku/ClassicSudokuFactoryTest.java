package com.tagadvance.sudoku;

import org.junit.Assert;
import org.junit.Test;

public class ClassicSudokuFactoryTest {

	@Test
	public void testCreateEmptySudokuIsNotNull() {
		SudokuFactory factory = new ClassicSudokuFactory();
		Sudoku sudoku = factory.createEmptySudoku();
		Assert.assertNotNull("sudoku is null", sudoku);
	}

	@Test
	public void testCreateEmptySudokuIsEmpty() {
		SudokuFactory factory = new ClassicSudokuFactory();
		Sudoku sudoku = factory.createEmptySudoku();
		// TODO
	}

}
