package com.tagadvance.sudoku;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import java.util.BitSet;
import java.util.Set;

/**
 * A sudoku defined by its value alphabet and a set of scopes. Nothing here assumes 9x9 or digits;
 * the scopes carry the whole shape of the puzzle.
 */
public class CompositeSudoku implements Sudoku {

	private final ImmutableSet<Character> values;
	private final ImmutableSet<Scope> scopeSet;

	private final Scope[] scopes;

	/**
	 * Symbol by ordinal, and the reverse: the bit for a stored char. Sized to the largest symbol,
	 * with every other entry left at zero -- including EMPTY, so an unfilled position contributes
	 * nothing to a used-mask without needing a branch.
	 */
	private final char[] symbols;
	private final int[] bitByChar;

	/**
	 * Every position that shares a scope with a given position. Precomputed geometry: the union of
	 * a position's scopes is what constrains it, and asking the peers directly skips re-walking
	 * three overlapping scopes on every lookup.
	 */
	private final int[][] peers;

	private final int fullMask;

	/**
	 * @param values the alphabet, one symbol per row, column and block
	 * @param scopeSet the row, column and block constraints, usually from a {@link ScopeFactory}
	 */
	public CompositeSudoku(final Set<Character> values, final Set<Scope> scopeSet) {
		super();
		this.values = ImmutableSet.copyOf(checkNotNull(values, "values must not be null"));
		this.scopeSet = ImmutableSet.copyOf(checkNotNull(scopeSet, "scopeSet must not be null"));

		checkArgument(!this.values.isEmpty(), "values must not be empty");
		checkArgument(this.values.size() <= Integer.SIZE, "at most %s values, but got %s",
			Integer.SIZE, this.values.size());

		this.symbols = new char[this.values.size()];
		int ordinal = 0, maxChar = 0;
		for (final char symbol : this.values) {
			symbols[ordinal++] = symbol;
			maxChar = Math.max(maxChar, symbol);
		}

		this.bitByChar = new int[maxChar + 1];
		for (int k = 0; k < symbols.length; k++) {
			bitByChar[symbols[k]] = 1 << k;
		}
		this.fullMask = (1 << symbols.length) - 1;

		this.scopes = this.scopeSet.toArray(new Scope[0]);

		int positionCount = 0;
		for (final var scope : scopes) {
			final var positions = scope.getPositions();
			for (int i = 0, n = positions.length(); i < n; i++) {
				positionCount = Math.max(positionCount, positions.get(i) + 1);
			}
		}

		final var peerBits = new BitSet[positionCount];
		for (int i = 0; i < positionCount; i++) {
			peerBits[i] = new BitSet(positionCount);
		}
		for (final var scope : scopes) {
			final var positions = scope.getPositions();
			for (int i = 0, n = positions.length(); i < n; i++) {
				for (int j = 0; j < n; j++) {
					if (i != j) {
						peerBits[positions.get(i)].set(positions.get(j));
					}
				}
			}
		}

		this.peers = new int[positionCount][];
		for (int i = 0; i < positionCount; i++) {
			peers[i] = peerBits[i].stream().toArray();
		}
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
	public char symbol(final int ordinal) {
		return symbols[ordinal];
	}

	@Override
	public int candidates(final Grid grid, final int position) {
		int used = 0;
		for (final int peer : peers[position]) {
			final char value = grid.get(peer);
			if (value < bitByChar.length) {
				used |= bitByChar[value];
			}
		}

		return fullMask & ~used;
	}

	@Override
	public boolean isValid(final Grid grid) {
		for (final var scope : scopes) {
			final var positions = scope.getPositions();
			int seen = 0;
			for (int i = 0, n = positions.length(); i < n; i++) {
				final int bit = bitFor(grid.get(positions.get(i)));
				if ((seen & bit) != 0) {
					return false;
				}
				seen |= bit;
			}
		}

		return true;
	}

	@Override
	public boolean isSolved(final Grid grid) {
		for (final var scope : scopes) {
			final var positions = scope.getPositions();
			int seen = 0;
			for (int i = 0, n = positions.length(); i < n; i++) {
				seen |= bitFor(grid.get(positions.get(i)));
			}
			if (Integer.bitCount(seen) != positions.length()) {
				return false;
			}
		}

		return true;
	}

	private int bitFor(final char value) {
		return value < bitByChar.length ? bitByChar[value] : 0;
	}

}
