package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.tagadvance.geometry.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A sudoku defined by its value alphabet and a set of scopes. Nothing here assumes 9x9 or digits;
 * the scopes carry the whole shape of the puzzle.
 */
public class CompositeSudoku implements Sudoku {

	private final ImmutableSet<Character> values;
	private final ImmutableSet<Scope> scopeSet;

	/**
	 * The scopes covering each position. Pure geometry, so it is computed once here and shared by
	 * every grid -- there is nothing per-grid left to cache.
	 */
	private final ImmutableMap<Point, ImmutableList<Scope>> scopesByPoint;

	/**
	 * @param values the alphabet, one symbol per row, column and block
	 * @param scopeSet the row, column and block constraints, usually from a {@link ScopeFactory}
	 */
	public CompositeSudoku(final Set<Character> values, final Set<Scope> scopeSet) {
		super();
		this.values = ImmutableSet.copyOf(checkNotNull(values, "values must not be null"));
		this.scopeSet = ImmutableSet.copyOf(checkNotNull(scopeSet, "scopeSet must not be null"));

		final Map<Point, List<Scope>> byPoint = new HashMap<>();
		for (final var scope : this.scopeSet) {
			for (final var point : scope.getPoints()) {
				byPoint.computeIfAbsent(point, p -> new ArrayList<>()).add(scope);
			}
		}
		this.scopesByPoint = byPoint.entrySet()
			.stream()
			.collect(ImmutableMap.toImmutableMap(Map.Entry::getKey,
				e -> ImmutableList.copyOf(e.getValue())));
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
	// TODO: a HashSet of boxed Characters per call is the hot path; wants a bitmask
	public Set<Character> getPotentialValues(final Grid grid, final Point point) {
		final var values = new HashSet<>(this.values);
		for (final var scope : scopesByPoint.getOrDefault(point, ImmutableList.of())) {
			values.removeAll(scope.getUsedValues(grid));
		}

		return values;
	}

}
