package com.tagadvance.sudoku;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableSet;
import com.tagadvance.geometry.Dimension;
import com.tagadvance.geometry.Rectangle;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
class SquareRootScopeFactoryTest {

	@Test
	void test1x1() {
		final var factory = new SquareRootScopeFactory();

		final int x = 0, y = 0, width = 1, height = 1;
		final var expectedScopes = ImmutableSet.<Scope<Void>>of(
			new RectangleScope<>(new Rectangle(x, y, width, height)));

		final var grid = (Grid<Void>) mock(Grid.class);
		final var size = new Dimension(width, height);
		when(grid.getSize()).thenReturn(size);
		final var scopes = factory.createScopes(grid);

		assertEquals(expectedScopes.size(), scopes.size());
		assertTrue(expectedScopes.containsAll(scopes));
	}

	@Test
	void test2x2() {
		final var factory = new SquareRootScopeFactory();

		final int width = 2, height = 2;
		final var expectedScopes = ImmutableSet.<Scope<Void>>of(
			// rows
			new RectangleScope<>(new Rectangle(0, 0, width, 1)),
			new RectangleScope<>(new Rectangle(0, 1, width, 1)),
			// columns
			new RectangleScope<>(new Rectangle(0, 0, 1, height)),
			new RectangleScope<>(new Rectangle(1, 0, 1, height)));

		final var grid = (Grid<Void>) mock(Grid.class);
		final var size = new Dimension(width, height);
		when(grid.getSize()).thenReturn(size);
		final var scopes = factory.createScopes(grid);

		assertEquals(expectedScopes.size(), scopes.size());
		assertTrue(expectedScopes.containsAll(scopes));
	}

	@Test
	void test3x3() {
		final var factory = new SquareRootScopeFactory();

		final int width = 3, height = 3;
		final var expectedScopes = ImmutableSet.<Scope<Void>>of(
			// rows
			new RectangleScope<>(new Rectangle(0, 0, width, 1)),
			new RectangleScope<>(new Rectangle(0, 1, width, 1)),
			new RectangleScope<>(new Rectangle(0, 2, width, 1)),
			// columns
			new RectangleScope<>(new Rectangle(0, 0, 1, height)),
			new RectangleScope<>(new Rectangle(1, 0, 1, height)),
			new RectangleScope<>(new Rectangle(2, 0, 1, height)));

		final var grid = (Grid<Void>) mock(Grid.class);
		final var size = new Dimension(width, height);
		when(grid.getSize()).thenReturn(size);
		final var scopes = factory.createScopes(grid);

		assertEquals(expectedScopes.size(), scopes.size());
		assertTrue(expectedScopes.containsAll(scopes));
	}

	@Test
	void test4x4() {
		final var factory = new SquareRootScopeFactory();

		final int width = 4, height = 4, blockSize = 2;
		ImmutableSet<Scope<Void>> expectedScopes = ImmutableSet.of(
			// rows
			new RectangleScope<>(new Rectangle(0, 0, width, 1)),
			new RectangleScope<>(new Rectangle(0, 1, width, 1)),
			new RectangleScope<>(new Rectangle(0, 2, width, 1)),
			new RectangleScope<>(new Rectangle(0, 3, width, 1)),
			// columns
			new RectangleScope<>(new Rectangle(0, 0, 1, height)),
			new RectangleScope<>(new Rectangle(1, 0, 1, height)),
			new RectangleScope<>(new Rectangle(2, 0, 1, height)),
			new RectangleScope<>(new Rectangle(3, 0, 1, height)),
			// blocks
			new RectangleScope<>(new Rectangle(0, 0, blockSize, blockSize)),
			new RectangleScope<>(new Rectangle(2, 0, blockSize, blockSize)),
			new RectangleScope<>(new Rectangle(0, 2, blockSize, blockSize)),
			new RectangleScope<>(new Rectangle(2, 2, blockSize, blockSize)));

		final var grid = (Grid<Void>) mock(Grid.class);
		final var size = new Dimension(width, height);
		when(grid.getSize()).thenReturn(size);
		final var scopes = factory.createScopes(grid);

		assertEquals(expectedScopes.size(), scopes.size());
		assertTrue(expectedScopes.containsAll(scopes));
	}

	@Test
	void test9x9() {
		final var factory = new SquareRootScopeFactory();

		final int width = 9, height = 9, blockSize = 3;
		final var expectedScopes = ImmutableSet.<Scope<Void>>of(
			// rows
			new RectangleScope<>(new Rectangle(0, 0, width, 1)),
			new RectangleScope<>(new Rectangle(0, 1, width, 1)),
			new RectangleScope<>(new Rectangle(0, 2, width, 1)),
			new RectangleScope<>(new Rectangle(0, 3, width, 1)),
			new RectangleScope<>(new Rectangle(0, 4, width, 1)),
			new RectangleScope<>(new Rectangle(0, 5, width, 1)),
			new RectangleScope<>(new Rectangle(0, 6, width, 1)),
			new RectangleScope<>(new Rectangle(0, 7, width, 1)),
			new RectangleScope<>(new Rectangle(0, 8, width, 1)),
			// columns
			new RectangleScope<>(new Rectangle(0, 0, 1, height)),
			new RectangleScope<>(new Rectangle(1, 0, 1, height)),
			new RectangleScope<>(new Rectangle(2, 0, 1, height)),
			new RectangleScope<>(new Rectangle(3, 0, 1, height)),
			new RectangleScope<>(new Rectangle(4, 0, 1, height)),
			new RectangleScope<>(new Rectangle(5, 0, 1, height)),
			new RectangleScope<>(new Rectangle(6, 0, 1, height)),
			new RectangleScope<>(new Rectangle(7, 0, 1, height)),
			new RectangleScope<>(new Rectangle(8, 0, 1, height)),
			// blocks
			new RectangleScope<>(new Rectangle(0, 0, blockSize, blockSize)),
			new RectangleScope<>(new Rectangle(3, 0, blockSize, blockSize)),
			new RectangleScope<>(new Rectangle(6, 0, blockSize, blockSize)),
			new RectangleScope<>(new Rectangle(0, 3, blockSize, blockSize)),
			new RectangleScope<>(new Rectangle(3, 3, blockSize, blockSize)),
			new RectangleScope<>(new Rectangle(6, 3, blockSize, blockSize)),
			new RectangleScope<>(new Rectangle(0, 6, blockSize, blockSize)),
			new RectangleScope<>(new Rectangle(3, 6, blockSize, blockSize)),
			new RectangleScope<>(new Rectangle(6, 6, blockSize, blockSize)));

		final var grid = (Grid<Void>) mock(Grid.class);
		final var size = new Dimension(width, height);
		when(grid.getSize()).thenReturn(size);
		final var scopes = factory.createScopes(grid);

		assertEquals(expectedScopes.size(), scopes.size());
		assertTrue(expectedScopes.containsAll(scopes));
	}

}
