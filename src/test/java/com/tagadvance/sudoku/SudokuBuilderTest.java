package com.tagadvance.sudoku;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.collect.ImmutableSet;
import com.tagadvance.geometry.Dimension;
import org.junit.jupiter.api.DisplayName;
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

	@Test
	@DisplayName("the alphabet decides the grid size")
	void alphabetDecidesSize() {
		final var factory = SudokuBuilder.newBuilder()
			.values("123456789ABCDEFG")
			.createSudokuFactory();

		assertEquals(new Dimension(16, 16), factory.createEmptyGrid().getSize());
		assertEquals(16, factory.createSudoku().getValues().size());
	}

	@Test
	@DisplayName("a 16x16 grid gets rows, columns and 4x4 blocks")
	void largeGridGetsBlockScopes() {
		final var sudoku = SudokuBuilder.newBuilder()
			.values("123456789ABCDEFG")
			.createSudokuFactory()
			.createSudoku();

		assertEquals(16 + 16 + 16, sudoku.getScopes().size());
	}

	@Test
	@DisplayName("rejects an alphabet containing the empty sentinel")
	void rejectsEmptySentinelAsValue() {
		final var builder = SudokuBuilder.newBuilder().values(ImmutableSet.of('1', Cell.EMPTY));
		final var e = assertThrows(IllegalArgumentException.class, builder::createSudokuFactory);

		assertEquals("values must not contain the empty sentinel", e.getMessage());
	}

}
