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

public class CompositeSudoku<V> implements Sudoku<V> {

	private final ImmutableSet<V> values;
	private final ImmutableSet<Scope<V>> scopeSet;

	/**
	 * cache of scopes for cell
	 */
	private final LoadingCache<GridCellPair<V>, ImmutableCollection<Scope<V>>> scopeCache = CacheBuilder.newBuilder()
		.build(new CacheLoader<>() {

			@Override
			public ImmutableCollection<Scope<V>> load(final GridCellPair<V> pair) {
				return getScopes().stream()
					.filter(scope -> scope.getCells(pair.grid).contains(pair.cell))
					.collect(ImmutableList.toImmutableList());
			}

		});

	/**
	 * @param values
	 * @param scopeSet
	 */
	protected CompositeSudoku(final ImmutableSet<V> values, final ImmutableSet<Scope<V>> scopeSet) {
		super();
		this.values = checkNotNull(values, "values must not be null");
		this.scopeSet = checkNotNull(scopeSet, "scopeSet must not be null");
	}

	@Override
	public Sudoku<V> copy() {
		return new CompositeSudoku<>(values, scopeSet);
	}

	@Override
	public ImmutableSet<V> getValues() {
		return values;
	}

	@Override
	public ImmutableSet<Scope<V>> getScopes() {
		return scopeSet;
	}

	@Override
	// TODO: Fix smelly code
	public Set<V> getPotentialValuesForCell(final Grid<V> grid, final Cell<V> cell) {
		final var values = new HashSet<>(this.values);
		getScopesForCell(grid, cell).stream()
			.map(scope -> scope.getUsedValues(grid))
			.flatMap(Collection::stream)
			.distinct()
			.forEach(values::remove);

		return values;
	}

	private ImmutableCollection<Scope<V>> getScopesForCell(final Grid<V> grid, final Cell<V> cell) {
		return scopeCache.getUnchecked(new GridCellPair<>(grid, cell));
	}

	private record GridCellPair<V>(Grid<V> grid, Cell<V> cell) {

		@Override
		public int hashCode() {
			return Objects.hash(grid, cell);
		}

		@Override
		public boolean equals(final Object o) {
			return o instanceof GridCellPair<?> that && Objects.equals(grid, that.grid)
				&& Objects.equals(cell, that.cell);
		}

	}

}
