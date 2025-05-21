package com.tagadvance.sudoku;

import com.google.common.collect.ImmutableCollection;
import com.tagadvance.geometry.Dimension;
import com.tagadvance.geometry.Point;

public interface Grid<V> extends Copyable<Grid<V>> {

	Dimension getSize();

	ImmutableCollection<Cell<V>> getCells();

	Cell<V> getCellAt(Point point);

}
