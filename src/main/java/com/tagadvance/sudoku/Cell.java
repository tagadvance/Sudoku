package com.tagadvance.sudoku;

public interface Cell<V> {

	boolean isEmpty();

	V getValue();

	void setValue(V value);

}
