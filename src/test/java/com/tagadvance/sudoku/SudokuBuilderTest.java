package com.tagadvance.sudoku;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class SudokuBuilderTest {

	@Test
	void newBuilderReturnsNewInstance() throws SecurityException {
		final var builder = SudokuBuilder.newBuilder();

		assertNotNull(builder);
	}

	@Test
	void createClassicSudokuFactoryReturnsNewInstance() throws SecurityException {
		final var builder = SudokuBuilder.newBuilder();
		final var factory = builder.createClassicSudokuFactory();

		assertNotNull(factory);
	}

	@Test
	void createEmptyGridReturnsNewInstance()
		throws SecurityException {
		final var builder = SudokuBuilder.newBuilder();
		final var factory = builder.createClassicSudokuFactory();
		final var grid = factory.createEmptyGrid();

		assertNotNull(grid);
	}

	@Test
	void createSudokuReturnsNewInstance() throws SecurityException {
		final var builder = SudokuBuilder.newBuilder();
		final var factory = builder.createClassicSudokuFactory();
		final var sudoku = factory.createSudoku();

		assertNotNull(sudoku);
	}

}
