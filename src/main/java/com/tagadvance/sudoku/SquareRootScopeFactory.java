package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkArgument;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.primes.Primes;

import com.google.common.collect.ImmutableSet;
import com.tagadvance.geometry.ImmutableDimension;

public class SquareRootScopeFactory implements ScopeFactory {

	public SquareRootScopeFactory() {
		super();
	}

	@Override
	public <V> ImmutableSet<Scope<V>> createScopes(Grid<V> grid) {
		ImmutableDimension size = grid.getSize();
		int width = size.getWidth(), height = size.getHeight();
		checkArgument(width == height, "grid must be square");

		List<Scope<V>> scopes = new ArrayList<>();

		// add blocks
		int blockSize = calculateBlockSize(width);
		if (blockSize > 0) {
			for (int y = 0, v = 0; v < blockSize; y += blockSize, v++) {
				for (int x = 0, h = 0; h < blockSize; x += blockSize, h++) {
					Rectangle rectangle = new Rectangle(x, y, blockSize, blockSize);
					RectangleScope<V> scope = new RectangleScope<>(rectangle);
					scopes.add(scope);
				}
			}
		}

		// add columns
		int x = 0, y = 0;
		int regionWidth = 1;
		for (x = 0; x < width; x++) {
			Rectangle rectangle = new Rectangle(x, y, regionWidth, height);
			RectangleScope<V> scope = new RectangleScope<>(rectangle);
			scopes.add(scope);
		}

		// add rows
		x = 0;
		y = 0;
		int regionHeight = 1;
		for (y = 0; y < height; y++) {
			Rectangle rectangle = new Rectangle(x, y, width, regionHeight);
			RectangleScope<V> scope = new RectangleScope<>(rectangle);
			scopes.add(scope);
		}

		return ImmutableSet.copyOf(scopes);
	}

	private int calculateBlockSize(int size) {
		checkArgument(size > 0, "size must be a positive integer");

		int blockSize = 0;
		if (!Primes.isPrime(size)) {
			blockSize = (int) Math.sqrt(size);
			int square = blockSize * blockSize;
			checkArgument(square == size, "size must be prime or a perfect square");
		}

		return blockSize;
	}

}
