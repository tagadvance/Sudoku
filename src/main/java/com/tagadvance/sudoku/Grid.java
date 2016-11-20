package com.tagadvance.sudoku;

import com.google.common.collect.ImmutableCollection;
import com.tagadvance.geometry.ImmutableDimension;
import com.tagadvance.geometry.ImmutablePoint;

public interface Grid<V> extends Copyable<Grid<V>> {

	public ImmutableDimension getSize();

	public ImmutableCollection<Cell<V>> getCells();

	public Cell<V> getCellAt(ImmutablePoint point);

}
