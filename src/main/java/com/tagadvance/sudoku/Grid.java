package com.tagadvance.sudoku;

import com.tagadvance.geometry.ImmutableDimension;
import com.tagadvance.geometry.ImmutablePoint;

public interface Grid<V> extends Copyable<Grid<V>> {

	public ImmutableDimension getSize();

	public Cell<V> getCellAt(ImmutablePoint point);

}
