package com.tagadvance.sudoku;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.collect.ImmutableSet;
import com.tagadvance.geometry.Dimension;
import com.tagadvance.geometry.Point;
import java.util.Set;
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
	@DisplayName("populates from any alphabet, treating everything else as blank")
	void populateWithArbitraryValues() {
		final Set<Character> values = ImmutableSet.of('A', 'B');
		final var grid = new FixedSizeGrid(new Dimension(2, 2));

		grid.populate("AB.?", values);

		assertEquals('A', grid.getCellAt(new Point(0, 0)).getValue());
		assertEquals('B', grid.getCellAt(new Point(1, 0)).getValue());
		assertEquals(2, grid.getEmptyCells().size());
	}

	@Test
	@DisplayName("prints a blank for every empty cell")
	void toStringRendersEmptyCells() {
		final var grid = new FixedSizeGrid(new Dimension(2, 2));
		grid.populate("A..B", ImmutableSet.of('A', 'B'));

		assertEquals(String.join(System.lineSeparator(), "A .", ". B"), grid.toString());
	}

	@Test
	@DisplayName("rejects a puzzle that is not one character per cell")
	void populateWrongLength() {
		final var grid = new FixedSizeGrid(new Dimension(2, 2));
		final var e = assertThrows(IllegalArgumentException.class,
			() -> grid.populate("AB", ImmutableSet.of('A', 'B')));

		assertEquals("puzzle must be 4 characters, but was 2", e.getMessage());
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
