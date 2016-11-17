package com.tagadvance.sudoku;

public interface Cell<V> {

	public boolean isEmpty();

	public V getValue();

	public void setValue(V value);

}
