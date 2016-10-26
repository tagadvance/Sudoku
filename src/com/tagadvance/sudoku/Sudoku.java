package com.tagadvance.sudoku;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

abstract class Sudoku implements Cloneable {

	public static final char EMPTY = '?';

	public static final byte MIN_SIZE = 4, MAX_SIZE = 25;

	private char[][] grid;
	public final int width, height;
	private Set<Scope> scopes;
	/**
	 * cache of scopes for cell
	 */
	private Map<Point, Set<Scope>> cellScopes;

	/**
	 * 
	 * @param possibleValues
	 * @throws IllegalArgumentException
	 */
	protected Sudoku(int width, int height) {
		super();

		this.width = width;
		this.height = height;
		createGrid();

		this.scopes = createScopes();
		this.cellScopes = new HashMap<Point, Set<Scope>>(grid.length);
	}

	protected Sudoku(Sudoku parent) {
		super();

		this.width = parent.width;
		this.height = parent.height;
		createGrid();
		for (int y = 0; y < grid.length; y++)
			for (int x = 0; x < grid[y].length; x++)
				grid[y][x] = parent.grid[y][x];

		this.scopes = createScopes();
		this.cellScopes = new HashMap<Point, Set<Scope>>(grid.length);
	}

	private void createGrid() {
		if (width < MIN_SIZE || width > MAX_SIZE)
			throw new IllegalArgumentException("invalid width " + width);
		else if (height < MIN_SIZE || height > MAX_SIZE)
			throw new IllegalArgumentException("invalid height " + height);
		this.grid = new char[height][width];
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public char getCellValue(int x, int y) {
		return this.grid[y][x];
	}

	public void setCellValue(int x, int y, char value) {
		this.grid[y][x] = prepareValue(value);
	}

	// private int translate(int x, int y) {
	// return (y * width) + x;
	// }

	protected char prepareValue(char c) {
		return Character.toLowerCase(c);
	}

	public Dimension getSize() {
		return new Dimension(width, height);
	}

	protected abstract char[] getPossibleValues();

	protected abstract Set<Scope> createScopes();

	public Set<Scope> getScopes() {
		return Collections.unmodifiableSet(this.scopes);
	}

	public Set<Scope> getScopesForCell(int x, int y) {
		Point p = new Point(x, y);
		if (cellScopes.containsKey(p)) {
			return cellScopes.get(p);
		}

		Set<Scope> scopes = new HashSet<Scope>();
		for (Scope scope : getScopes()) {
			if (scope.containsCell(p)) {
				scopes.add(scope);
			}
		}

		cellScopes.put(p, scopes);
		return scopes;
	}

	/**
	 * I would like to optimize this by calculating the potential values for every cell which shares
	 * a scope with the set cell when setCellValue is called
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<Character> getCellPotentialValues(int x, int y) {
		Set<Scope> scopes = getScopesForCell(x, y);
		Set<Character>[] characterSets = new Set[scopes.size()];
		int i = 0;
		for (Scope scope : scopes) {
			characterSets[i++] = scope.getUnusedValues();
		}
		return getCommonElements(characterSets);
	}

	public void clear() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				setCellValue(x, y, EMPTY);
			}
		}
	}

	public Sudoku solve() throws UnsolvableException {
		return new SudokuSolver(this).solve();
	}

	public List<Point> getEmptyCells() {
		List<Point> list = new ArrayList<Point>();
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				if (isCellEmpty(x, y))
					list.add(new Point(x, y));
		return list;
	}

	public boolean isCellEmpty(int x, int y) {
		char value = getCellValue(x, y);
		return isEmpty(value);
	}

	public boolean isEmpty(char value) {
		return (value == EMPTY);
	}

	public boolean isValid() {
		for (Scope scope : getScopes())
			if (!scope.isValid())
				return false;
		return true;
	}

	public void validate() throws UnsolvableException {
		for (Scope scope : getScopes())
			scope.validate();
	}

	public boolean isSolved() {
		for (Scope scope : getScopes())
			if (!scope.isSolved())
				return false;
		return true;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				char value = getCellValue(x, y);
				sb.append(value).append(" ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public Object clone() {
		throw new UnsupportedOperationException(new CloneNotSupportedException());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sudoku other = (Sudoku) obj;
		for (int y = 0; y < grid.length; y++) {
			if (!Arrays.equals(grid[y], other.grid[y]))
				return false;
		}
		return true;
	}

	public static Sudoku demo(Sudoku sudoku, String puzzle) {
		int x = 0, y = 0;
		for (int i = 0; i < puzzle.length(); i++) {
			sudoku.setCellValue(x, y, puzzle.charAt(i));
			if (++x >= sudoku.width) {
				y++;
				x = 0;
			}
		}
		return sudoku;
	}

	/**
	 * 
	 * @param <E>
	 * @param <C>
	 * @param collections
	 * @return elements common to all collections
	 */
	public static <E> Set<E> getCommonElements(Collection<E>... collections) {
		Set<E> common = new HashSet<E>();
		for (Collection<E> collection : collections)
			common.addAll(collection);
		for (Collection<E> collection : collections)
			common.retainAll(collection);
		return common;
	}

}
