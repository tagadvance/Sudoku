package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * A sudoku defined by its value alphabet and a set of scopes. Nothing here assumes 9x9 or digits;
 * the scopes carry the whole shape of the puzzle.
 */
public class CompositeSudoku implements Sudoku {

	private final ImmutableSet<Character> values;
	private final ImmutableSet<Scope> scopeSet;

	/**
	 * Cache of the scopes each cell belongs to. A cell belongs to exactly one grid, so the cell
	 * alone identifies the entry; keying it weakly lets it die with that grid.
	 */
	private final ConcurrentMap<Cell, ImmutableCollection<Scope>> scopeCache = new MapMaker().weakKeys()
		.makeMap();

	/**
	 * @param values the alphabet, one symbol per row, column and block
	 * @param scopeSet the row, column and block constraints, usually from a {@link ScopeFactory}
	 */
	public CompositeSudoku(final Set<Character> values, final Set<Scope> scopeSet) {
		super();
		this.values = ImmutableSet.copyOf(checkNotNull(values, "values must not be null"));
		this.scopeSet = ImmutableSet.copyOf(checkNotNull(scopeSet, "scopeSet must not be null"));
	}

	@Override
	public Sudoku copy() {
		// don't bother as this class is immutable
		return this;
	}

	@Override
	public ImmutableSet<Character> getValues() {
		return values;
	}

	@Override
	public ImmutableSet<Scope> getScopes() {
		return scopeSet;
	}

	@Override
	// TODO: Fix smelly code
	public Set<Character> getPotentialValuesForCell(final Grid grid, final Cell cell) {
		final var values = new HashSet<>(this.values);
		getScopesForCell(grid, cell).stream()
			.map(scope -> scope.getUsedValues(grid))
			.flatMap(Collection::stream)
			.distinct()
			.forEach(values::remove);

		return values;
	}

	private ImmutableCollection<Scope> getScopesForCell(final Grid grid, final Cell cell) {
		return scopeCache.computeIfAbsent(cell, c -> getScopes().stream()
			.filter(scope -> scope.getCells(grid).contains(c))
			.collect(ImmutableList.toImmutableList()));
	}

}
