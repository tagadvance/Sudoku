package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.google.common.base.Objects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class CompositeSudoku<V> implements Sudoku<V> {

	private final ImmutableSet<V> values;
	private final ImmutableSet<Scope<V>> scopeSet;

	/**
	 * cache of scopes for cell
	 */
	private final LoadingCache<GridCellPair, ImmutableCollection<Scope<V>>> scopeCache = CacheBuilder.newBuilder().build(new ScopeCacheLoader());
	
	/**
	 * 
	 * @param possibleValues
	 * @throws IllegalArgumentException
	 */
	protected CompositeSudoku(ImmutableSet<V> values, ImmutableSet<Scope<V>> scopeSet) {
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
	public Set<V> getPotentialValuesForCell(Grid<V> grid, Cell<V> cell) {
		Set<V> potentialValues = new HashSet<>(values);

		// System.out.println(potentialValues);
		// System.out.println("values: " + Joiner.on(' ').join(potentialValues));

		ImmutableCollection<Scope<V>> scopeSet = getScopesForCell(grid, cell);
		for (Scope<V> scope : scopeSet) {
			Collection<V> usedValues = scope.getUsedValues(grid);
			// System.out.println("removing: " + Joiner.on(' ').join(usedValues));
			potentialValues.removeAll(usedValues);
		}
		// System.out.println("values: " + Joiner.on(' ').join(potentialValues));

		return potentialValues;
	}
	
	private ImmutableCollection<Scope<V>> getScopesForCell(Grid<V> grid, Cell<V> cell) {
		try {
			return scopeCache.get(new GridCellPair(grid, cell));
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
	
	private class ScopeCacheLoader extends CacheLoader<GridCellPair, ImmutableCollection<Scope<V>>> {

		@Override
		public ImmutableCollection<Scope<V>> load(GridCellPair pair) throws Exception {
			Set<Scope<V>> scopeSet = new HashSet<>();
			for (Scope<V> scope : getScopes()) {
				Collection<Cell<V>> cells = scope.getCells(pair.grid);
				if (cells.contains(pair.cell)) {
					scopeSet.add(scope);
				}
			}
			return ImmutableList.copyOf(scopeSet);
		}

	}
	
	private class GridCellPair {
		
		private final Grid<V> grid;
		private final Cell<V> cell;
		
		public GridCellPair(Grid<V> grid, Cell<V> cell) {
			super();
			this.grid = grid;
			this.cell = cell;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(grid, cell);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			@SuppressWarnings("unchecked")
			GridCellPair other = (GridCellPair) obj;
			return Objects.equal(grid, other.grid) && Objects.equal(cell, other.cell);
		}
		
	}

}
