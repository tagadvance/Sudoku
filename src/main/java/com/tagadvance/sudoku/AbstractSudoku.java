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

import com.google.common.base.Preconditions;

public abstract class AbstractSudoku implements Cloneable {

	public static final String EMPTY = "?";

	public static final byte MIN_SIZE = 1, MAX_SIZE = 25;

	private String[][] grid;
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
	protected AbstractSudoku(int width, int height) {
		super();

		createGrid(width, height);
		this.width = width;
		this.height = height;

		this.scopes = createScopes();
		this.cellScopes = new HashMap<Point, Set<Scope>>(grid.length);
	}

	protected AbstractSudoku(AbstractSudoku parent) {
		super();

		createGrid(parent.width, parent.height);
		for (int y = 0; y < grid.length; y++) {
			for (int x = 0; x < grid[y].length; x++) {
				grid[y][x] = parent.grid[y][x];
			}
		}
		this.width = parent.width;
		this.height = parent.height;

		this.scopes = createScopes();
		this.cellScopes = new HashMap<Point, Set<Scope>>(grid.length);
	}

	private void createGrid(int width, int height) {
		Preconditions.checkArgument(width < MIN_SIZE, "width must be >= " + MIN_SIZE);
		Preconditions.checkArgument(width > MAX_SIZE, "width must be <= " + MAX_SIZE);
		Preconditions.checkArgument(height < MIN_SIZE, "height must be >= " + MIN_SIZE);
		Preconditions.checkArgument(height > MAX_SIZE, "height must be <= " + MAX_SIZE);
		this.grid = new String[height][width];
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public String getCellValue(int x, int y) {
		return this.grid[y][x];
	}

	public void setCellValue(int x, int y, String value) {
		this.grid[y][x] = prepareValue(value);
	}

	// private int translate(int x, int y) {
	// return (y * width) + x;
	// }

	protected String prepareValue(String value) {
		return value.toLowerCase();
	}

	public Dimension getSize() {
		return new Dimension(width, height);
	}

	protected abstract String[] getPossibleValues();

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
	public Set<String> getCellPotentialValues(int x, int y) {
		Set<Scope> scopes = getScopesForCell(x, y);
		Set<String>[] stringSets = new Set[scopes.size()];
		int i = 0;
		for (Scope scope : scopes) {
			stringSets[i++] = scope.getUnusedValues();
		}
		return getCommonElements(stringSets);
	}

	public void clear() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				setCellValue(x, y, EMPTY);
			}
		}
	}

	public AbstractSudoku solve() throws UnsolvableException {
		return new FastSudokuSolver(this).solve();
	}

	public List<Point> getEmptyCells() {
		List<Point> list = new ArrayList<Point>();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (isCellEmpty(x, y)) {
					list.add(new Point(x, y));
				}
			}
		}
		return list;
	}

	public boolean isCellEmpty(int x, int y) {
		String value = getCellValue(x, y);
		return isEmpty(value);
	}

	public boolean isEmpty(String value) {
		return (value == null || value.isEmpty() || value.equals(EMPTY));
	}

	public boolean isValid() {
		for (Scope scope : getScopes()) {
			if (!scope.isValid()) {
				return false;
			}
		}
		return true;
	}

	public void validate() throws UnsolvableException {
		for (Scope scope : getScopes()) {
			scope.validate();
		}
	}

	public boolean isSolved() {
		for (Scope scope : getScopes()) {
			if (!scope.isSolved()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				String value = getCellValue(x, y);
				sb.append(value).append(" ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	@Override
	public Object clone() {
		throw new UnsupportedOperationException(new CloneNotSupportedException());
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
		AbstractSudoku other = (AbstractSudoku) obj;
		for (int y = 0; y < grid.length; y++) {
			if (!Arrays.equals(grid[y], other.grid[y])) {
				return false;
			}
		}
		return true;
	}

	public static AbstractSudoku demo(AbstractSudoku sudoku, String puzzle) {
		int x = 0, y = 0;
		for (int i = 0; i < puzzle.length(); i++) {
			String value = puzzle.substring(i, i + 1);
			sudoku.setCellValue(x, y, value);
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
		for (Collection<E> collection : collections) {
			common.addAll(collection);
		}
		for (Collection<E> collection : collections) {
			common.retainAll(collection);
		}
		return common;
	}

}
