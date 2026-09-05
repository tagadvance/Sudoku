package com.tagadvance.sudoku;

import com.google.common.collect.ImmutableSet;

public interface ScopeFactory {

	 ImmutableSet<Scope> createScopes(Grid grid);

}
