package com.tagadvance.sudoku;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
class AtomicSolutionTest {

	@Test
	void testGetSolutionThrowsUnsolvableException() {
		assertThrows(UnsolvableException.class, () -> new AtomicSolution<>().getSolution());
	}

	@Test
	void testGetSolutionReturnsGrid() throws UnsolvableException {
		final var solution = new AtomicSolution<Void>();
		final var expectedGrid = (Grid<Void>) mock(Grid.class);
		solution.setSolution(expectedGrid, null);
		final var grid = solution.getSolution();

		assertNotNull(grid);
	}

	@Test
	void testGetSolutionThrowsCustomUnsolvableException() {
		final var solution = new AtomicSolution<Void>();
		final var expectedException = new UnsolvableException("foobar");
		solution.setSolution(null, expectedException);

		assertThrows(UnsolvableException.class, solution::getSolution, "foobar");
	}

	@Test
	void testGetSolutionUnsolvableExceptionTakesPrecedenceOverGrid() {
		final var solution = new AtomicSolution<Void>();
		final var grid = (Grid<Void>) mock(Grid.class);
		final var expectedException = new UnsolvableException("foobar");
		solution.setSolution(grid, expectedException);

		assertThrows(UnsolvableException.class, solution::getSolution, "foobar");
	}

	@Test
	void testToStringIsNotNull() {
		final var solution = new AtomicSolution<Void>();
		final var string = solution.toString();

		assertNotNull(string);
	}

}
