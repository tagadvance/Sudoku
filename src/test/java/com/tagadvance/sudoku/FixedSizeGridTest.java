package com.tagadvance.sudoku;

import static org.junit.jupiter.api.Assertions.*;

import com.tagadvance.geometry.Dimension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FixedSizeGridTest {

	@Test
	@DisplayName("a newly created grid holds nothing but empty cells")
	void freshGridIsEmpty() {
		final int width = 9, height = 9;
		final var grid = new FixedSizeGrid(new Dimension(width, height));

		assertEquals(width * height, grid.getEmptyCells().size());
	}

	@Test
	@DisplayName("rejects a width below the minimum and names the bound")
	void widthTooSmall() {
		final var size = new Dimension(FixedSizeGrid.MIN_SIZE - 1, 9);
		final var e = assertThrows(IllegalArgumentException.class, () -> new FixedSizeGrid(size));

		assertEquals("width must be >= 1", e.getMessage());
	}

	@Test
	@DisplayName("rejects a height above the maximum and names the bound")
	void heightTooLarge() {
		final var size = new Dimension(9, FixedSizeGrid.MAX_SIZE + 1);
		final var e = assertThrows(IllegalArgumentException.class, () -> new FixedSizeGrid(size));

		assertEquals("height must be <= 25", e.getMessage());
	}

}
