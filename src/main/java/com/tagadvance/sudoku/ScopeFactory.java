package com.tagadvance.sudoku;

import com.google.common.collect.ImmutableSet;

public interface ScopeFactory {

	public <V> ImmutableSet<Scope<V>> createScopes(Grid<V> grid);

}
