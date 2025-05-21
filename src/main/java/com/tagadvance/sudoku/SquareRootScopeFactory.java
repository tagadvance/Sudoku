package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.ImmutableSet;
import com.tagadvance.geometry.Rectangle;
import java.util.ArrayList;
import org.apache.commons.math3.primes.Primes;

public class SquareRootScopeFactory implements ScopeFactory {

	public SquareRootScopeFactory() {
		super();
	}

	@Override
	public <V> ImmutableSet<Scope<V>> createScopes(final Grid<V> grid) {
		final var size = grid.getSize();
		final int width = size.width(), height = size.height();
		checkArgument(width == height, "grid must be square");

		final var scopes = new ArrayList<Scope<V>>();

		// add blocks
		int blockSize = calculateBlockSize(width);
		if (blockSize > 0) {
			for (int y = 0, v = 0; v < blockSize; y += blockSize, v++) {
				for (int x = 0, h = 0; h < blockSize; x += blockSize, h++) {
					final var rectangle = new Rectangle(x, y, blockSize, blockSize);
					final var scope = new RectangleScope<V>(rectangle);
					scopes.add(scope);
				}
			}
		}

		// add columns
		int x = 0, y = 0;
		final int regionWidth = 1;
		for (x = 0; x < width; x++) {
			final var rectangle = new Rectangle(x, y, regionWidth, height);
			final var scope = new RectangleScope<V>(rectangle);
			scopes.add(scope);
		}

		// add rows
		x = 0;
		int regionHeight = 1;
		for (y = 0; y < height; y++) {
			final var rectangle = new Rectangle(x, y, width, regionHeight);
			final var scope = new RectangleScope<V>(rectangle);
			scopes.add(scope);
		}

		return ImmutableSet.copyOf(scopes);
	}

	private int calculateBlockSize(final int size) {
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
