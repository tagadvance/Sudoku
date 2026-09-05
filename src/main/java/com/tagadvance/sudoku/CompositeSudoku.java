package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CompositeSudoku implements Sudoku {

	private final ImmutableSet<Character> values;
	private final ImmutableSet<Scope> scopeSet;

	/**
	 * cache of scopes for cell
	 */
	private final LoadingCache<GridCellPair, ImmutableCollection<Scope>> scopeCache = CacheBuilder.newBuilder()
		.build(new CacheLoader<>() {

			@Override
			public ImmutableCollection<Scope> load(final GridCellPair pair) {
				return getScopes().stream()
					.filter(scope -> scope.getCells(pair.grid).contains(pair.cell))
					.collect(ImmutableList.toImmutableList());
			}

		});

	/**
	 * @param values
	 * @param scopeSet
	 */
	protected CompositeSudoku(final ImmutableSet<Character> values,
		final ImmutableSet<Scope> scopeSet) {
		super();
		this.values = checkNotNull(values, "values must not be null");
		this.scopeSet = checkNotNull(scopeSet, "scopeSet must not be null");
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
		return scopeCache.getUnchecked(new GridCellPair(grid, cell));
	}

	private record GridCellPair(Grid grid, Cell cell) {

		@Override
		public int hashCode() {
			return Objects.hash(grid, cell);
		}

		@Override
		public boolean equals(final Object o) {
			return o instanceof GridCellPair that && Objects.equals(grid, that.grid)
				&& Objects.equals(cell, that.cell);
		}

	}

}
