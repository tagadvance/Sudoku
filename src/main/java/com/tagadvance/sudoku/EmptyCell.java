package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class EmptyCell<V> implements Cell<V> {

	private Set<V> emptyValues;
	private V value;

	public EmptyCell() {
		super();
		this.emptyValues = new HashSet<>();
	}

	public EmptyCell(ImmutableSet<V> emptyValues) {
		super();
		this.emptyValues = checkNotNull(emptyValues);
	}

	@Override
	public boolean isEmpty() {
		return this.value == null || emptyValues.contains(value);
	}

	@Override
	public V getValue() {
		return this.value;
	}

	@Override
	public void setValue(V value) {
		this.value = value;
	}

}
