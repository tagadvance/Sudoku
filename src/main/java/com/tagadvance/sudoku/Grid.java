package com.tagadvance.sudoku;

import com.google.common.collect.ImmutableCollection;
import com.tagadvance.geometry.Dimension;
import com.tagadvance.geometry.Point;
import java.util.List;
import java.util.stream.Collectors;

public interface Grid<V> extends Copyable<Grid<V>> {

	Dimension getSize();

	default List<Cell<V>> getEmptyCells() {
		return getCells()
			.stream()
			.filter(Cell::isEmpty)
			.collect(Collectors.toList());
	}

	ImmutableCollection<Cell<V>> getCells();

	Cell<V> getCellAt(Point point);

}
