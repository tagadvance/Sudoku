package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.tagadvance.geometry.ImmutablePoint;

public class CompositeSudoku<V> implements Sudoku<V> {

	private final ImmutableSet<V> values;
	private final ImmutableSet<Scope<V>> scopeSet;

	/**
	 * cache of scopes for cell
	 */
	private final LoadingCache<ImmutablePoint, ImmutableSet<Scope<V>>> cellScopes;

	/**
	 * 
	 * @param possibleValues
	 * @throws IllegalArgumentException
	 */
	protected CompositeSudoku(ImmutableSet<V> values, ImmutableSet<Scope<V>> scopeSet) {
		super();
		this.values = checkNotNull(values, "values must not be null");
		this.scopeSet = checkNotNull(scopeSet, "scopeSet must not be null");

		this.cellScopes = CacheBuilder.newBuilder().build(new ScopeCacheLoader());
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
		return this.scopeSet;
	}

	@Override
	public ImmutableSet<Scope<V>> getScopesForPoint(ImmutablePoint point) {
		try {
			return cellScopes.get(point);
		} catch (ExecutionException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public boolean isValid(Grid<V> grid) {
		for (Scope<V> scope : getScopes()) {
			if (!scope.isValid(grid)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isSolved(Grid<V> grid) {
		for (Scope<V> scope : getScopes()) {
			if (!scope.isSolved(grid)) {
				return false;
			}
		}
		return true;
	}

	private class ScopeCacheLoader extends CacheLoader<ImmutablePoint, ImmutableSet<Scope<V>>> {

		@Override
		public ImmutableSet<Scope<V>> load(ImmutablePoint point) throws Exception {
			Set<Scope<V>> scopeSet = new HashSet<>();
			for (Scope<V> scope : getScopes()) {
				ImmutableSet<ImmutablePoint> points = scope.getCellPoints();
				if (points.contains(point)) {
					scopeSet.add(scope);
				}
			}

			return ImmutableSet.copyOf(scopeSet);
		}

	}

}
