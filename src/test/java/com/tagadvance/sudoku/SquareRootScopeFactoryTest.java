package com.tagadvance.sudoku;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Rectangle;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.tagadvance.geometry.Dimension;
import com.tagadvance.geometry.ImmutableDimension;

public class SquareRootScopeFactoryTest {

	@Test
	public void test1x1() {
		SquareRootScopeFactory factory = new SquareRootScopeFactory();

		int x = 0, y = 0, width = 1, height = 1;
		ImmutableSet<Scope<Void>> expectedScopes =
				ImmutableSet.of(new RectangleScope<>(new Rectangle(x, y, width, height)));

		@SuppressWarnings("unchecked")
		Grid<Void> grid = mock(Grid.class);
		ImmutableDimension size = new Dimension(width, height);
		when(grid.getSize()).thenReturn(size);
		ImmutableSet<Scope<Void>> scopes = factory.createScopes(grid);

		Assert.assertEquals(expectedScopes.size(), scopes.size());
		Assert.assertTrue(expectedScopes.containsAll(scopes));
	}

	@Test
	public void test2x2() {
		SquareRootScopeFactory factory = new SquareRootScopeFactory();

		int width = 2, height = 2;
		ImmutableSet<Scope<Void>> expectedScopes = ImmutableSet.of(
				// rows
				new RectangleScope<>(new Rectangle(0, 0, width, 1)), new RectangleScope<>(new Rectangle(0, 1, width, 1)),
				// columns
				new RectangleScope<>(new Rectangle(0, 0, 1, height)), new RectangleScope<>(new Rectangle(1, 0, 1, height)));

		@SuppressWarnings("unchecked")
		Grid<Void> grid = mock(Grid.class);
		ImmutableDimension size = new Dimension(width, height);
		when(grid.getSize()).thenReturn(size);
		ImmutableSet<Scope<Void>> scopes = factory.createScopes(grid);

		Assert.assertEquals(expectedScopes.size(), scopes.size());
		Assert.assertTrue(expectedScopes.containsAll(scopes));
	}

	@Test
	public void test3x3() {
		SquareRootScopeFactory factory = new SquareRootScopeFactory();

		int width = 3, height = 3;
		ImmutableSet<Scope<Void>> expectedScopes = ImmutableSet.of(
				// rows
				new RectangleScope<>(new Rectangle(0, 0, width, 1)), new RectangleScope<>(new Rectangle(0, 1, width, 1)),
				new RectangleScope<>(new Rectangle(0, 2, width, 1)),
				// columns
				new RectangleScope<>(new Rectangle(0, 0, 1, height)), new RectangleScope<>(new Rectangle(1, 0, 1, height)),
				new RectangleScope<>(new Rectangle(2, 0, 1, height)));

		@SuppressWarnings("unchecked")
		Grid<Void> grid = mock(Grid.class);
		ImmutableDimension size = new Dimension(width, height);
		when(grid.getSize()).thenReturn(size);
		ImmutableSet<Scope<Void>> scopes = factory.createScopes(grid);

		Assert.assertEquals(expectedScopes.size(), scopes.size());
		Assert.assertTrue(expectedScopes.containsAll(scopes));
	}

	@Test
	public void test4x4() {
		SquareRootScopeFactory factory = new SquareRootScopeFactory();

		int width = 4, height = 4, blockSize = 2;
		ImmutableSet<Scope<Void>> expectedScopes = ImmutableSet.of(
				// rows
				new RectangleScope<>(new Rectangle(0, 0, width, 1)), new RectangleScope<>(new Rectangle(0, 1, width, 1)),
				new RectangleScope<>(new Rectangle(0, 2, width, 1)), new RectangleScope<>(new Rectangle(0, 3, width, 1)),
				// columns
				new RectangleScope<>(new Rectangle(0, 0, 1, height)), new RectangleScope<>(new Rectangle(1, 0, 1, height)),
				new RectangleScope<>(new Rectangle(2, 0, 1, height)), new RectangleScope<>(new Rectangle(3, 0, 1, height)),
				// blocks
				new RectangleScope<>(new Rectangle(0, 0, blockSize, blockSize)),
				new RectangleScope<>(new Rectangle(2, 0, blockSize, blockSize)),
				new RectangleScope<>(new Rectangle(0, 2, blockSize, blockSize)),
				new RectangleScope<>(new Rectangle(2, 2, blockSize, blockSize)));

		@SuppressWarnings("unchecked")
		Grid<Void> grid = mock(Grid.class);
		ImmutableDimension size = new Dimension(width, height);
		when(grid.getSize()).thenReturn(size);
		ImmutableSet<Scope<Void>> scopes = factory.createScopes(grid);

		Assert.assertEquals(expectedScopes.size(), scopes.size());
		Assert.assertTrue(expectedScopes.containsAll(scopes));
	}

	@Test
	public void test9x9() {
		SquareRootScopeFactory factory = new SquareRootScopeFactory();

		int width = 9, height = 9, blockSize = 3;
		ImmutableSet<Scope<Void>> expectedScopes = ImmutableSet.of(
				// rows
				new RectangleScope<>(new Rectangle(0, 0, width, 1)), new RectangleScope<>(new Rectangle(0, 1, width, 1)),
				new RectangleScope<>(new Rectangle(0, 2, width, 1)), new RectangleScope<>(new Rectangle(0, 3, width, 1)),
				new RectangleScope<>(new Rectangle(0, 4, width, 1)), new RectangleScope<>(new Rectangle(0, 5, width, 1)),
				new RectangleScope<>(new Rectangle(0, 6, width, 1)), new RectangleScope<>(new Rectangle(0, 7, width, 1)),
				new RectangleScope<>(new Rectangle(0, 8, width, 1)),
				// columns
				new RectangleScope<>(new Rectangle(0, 0, 1, height)), new RectangleScope<>(new Rectangle(1, 0, 1, height)),
				new RectangleScope<>(new Rectangle(2, 0, 1, height)), new RectangleScope<>(new Rectangle(3, 0, 1, height)),
				new RectangleScope<>(new Rectangle(4, 0, 1, height)), new RectangleScope<>(new Rectangle(5, 0, 1, height)),
				new RectangleScope<>(new Rectangle(6, 0, 1, height)), new RectangleScope<>(new Rectangle(7, 0, 1, height)),
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

		@SuppressWarnings("unchecked")
		Grid<Void> grid = mock(Grid.class);
		ImmutableDimension size = new Dimension(width, height);
		when(grid.getSize()).thenReturn(size);
		ImmutableSet<Scope<Void>> scopes = factory.createScopes(grid);

		Assert.assertEquals(expectedScopes.size(), scopes.size());
		Assert.assertTrue(expectedScopes.containsAll(scopes));
	}

}
