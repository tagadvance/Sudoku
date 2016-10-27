package com.tagadvance.sudoku;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.base.Preconditions;

public class FastSudokuSolver {

	private AbstractSudoku sudoku;

	private ExecutorService threadPool;
	private final Object lock = new Object();
	private volatile boolean shutdown;
	private volatile int callCount;
	private long start;
	private List<Future<AbstractSudoku>> futureList;

	public FastSudokuSolver(AbstractSudoku sudoku) {
		super();
		setSudoku(sudoku);
	}

	public AbstractSudoku getSudoku() {
		return sudoku;
	}

	public void setSudoku(AbstractSudoku sudoku) {
		Preconditions.checkNotNull(sudoku, "sudoku must not be null");
		this.sudoku = sudoku.copy();
	}

	public AbstractSudoku solve() throws UnsolvableException {
		sudoku.validate();

		solveSingles();
		List<SudokuWorker> callList = fork();

		int callSize = callList.size();
		System.out.println("callList.size " + callSize);

		Runtime runtime = Runtime.getRuntime();
		int cores = runtime.availableProcessors();
		threadPool = Executors.newFixedThreadPool(cores);
		shutdown = false;

		callCount = 0;
		start = System.currentTimeMillis();

		this.futureList = new ArrayList<Future<AbstractSudoku>>();
		for (Callable<AbstractSudoku> call : callList) {
			Future<AbstractSudoku> future = threadPool.submit(call);
			futureList.add(future);
		}

		threadPool.shutdown();
		return join();
	}

	private void solveSingles() {
		Set<Point> singles = new HashSet<Point>();
		do {
			singles.clear();

			List<Point> emptyCells = sudoku.getEmptyCells();
			Map<Point, Integer> priorityMap = prioritize(sudoku, emptyCells);
			for (Map.Entry<Point, Integer> entry : priorityMap.entrySet()) {
				Point pt = entry.getKey();
				int potential = entry.getValue();
				if (potential == 1) {
					singles.add(pt);
				}
			}
			for (Point pt : singles) {
				Set<String> set = sudoku.getCellPotentialValues(pt.x, pt.y);
				String value = set.iterator().next();
				sudoku.setCellValue(pt.x, pt.y, value);
			}
		} while (!singles.isEmpty());
	}

	private List<SudokuWorker> fork() {
		List<SudokuWorker> callList = new ArrayList<SudokuWorker>();
		int depth = calculateDepth();
		fork(callList, depth);
		return callList;
	}

	private int calculateDepth() {
		return 4;
	}

	private void fork(List<SudokuWorker> callList, int depth) {
		if (depth <= 0) {
			callList.add(new SudokuWorker(sudoku));
			return;
		}

		List<Point> priorityList = getPriorityList(sudoku);
		Point pt = priorityList.get(0);

		Set<String> potentialCellValues = sudoku.getCellPotentialValues(pt.x, pt.y);
		for (String value : potentialCellValues) {
			sudoku.setCellValue(pt.x, pt.y, value);
			fork(callList, depth - 1);
		}

		sudoku.setCellValue(pt.x, pt.y, AbstractSudoku.EMPTY);
	}

	private AbstractSudoku join() {
		while (!threadPool.isTerminated()) {
			for (Iterator<Future<AbstractSudoku>> i = futureList.iterator(); i.hasNext();) {
				Future<AbstractSudoku> future = i.next();
				if (!future.isDone()) {
					continue;
				}

				i.remove();

				AbstractSudoku result = null;
				try {
					result = future.get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				if (result != null) {
					shutdown = true;
					return result;
				}
			}
			lock();
		}
		throw null;
	}

	private void lock() {
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
			}
		}
	}

	private void print() {
		System.out.println("callCount = " + callCount);
		double stop = System.currentTimeMillis();
		System.out.println((stop - start) / 1000 + " seconds");
	}

	private AbstractSudoku solve(AbstractSudoku sudoku, List<Point> cells) {
		if (shutdown) {
			return null;
		}
		if (sudoku.isSolved()) {
			return sudoku;
		}
		if (futureList.size() == 1) {
			try {
				return sudoku.solve();
			} catch (UnsolvableException e) {
				e.printStackTrace();
			}
		}
		if (cells == null) {
			cells = getPriorityList(sudoku);
		}
		if (cells.isEmpty()) {
			return null;
		}
		callCount++;

		Point p = cells.remove(0);

		Set<String> potentialCellValues = sudoku.getCellPotentialValues(p.x, p.y);
		for (String value : potentialCellValues) {
			sudoku.setCellValue(p.x, p.y, value);
			AbstractSudoku result = solve(sudoku, null);
			if (result != null) {
				return result;
			}
		}

		sudoku.setCellValue(p.x, p.y, AbstractSudoku.EMPTY);
		cells.add(0, p);

		return null;
	}

	private List<Point> getPriorityList(AbstractSudoku sudoku) {
		List<Point> emptyCells = sudoku.getEmptyCells();
		prioritize(sudoku, emptyCells);
		return emptyCells;
	}

	private Map<Point, Integer> prioritize(AbstractSudoku sudoku, List<Point> emptyCells) {
		final Map<Point, Integer> cells = new HashMap<Point, Integer>();
		for (Point p : emptyCells) {
			Set<String> potentialValues = sudoku.getCellPotentialValues(p.x, p.y);
			int size = potentialValues.size();
			cells.put(p, size);
			potentialValues.clear();
		}
		// sort from least to greatest
		Collections.sort(emptyCells, new Comparator<Point>() {
			@Override
			public int compare(Point p1, Point p2) {
				int size1 = cells.get(p1);
				int size2 = cells.get(p2);
				return (size1 == size2 ? 0 : (size1 < size2 ? -1 : 1));
			}
		});
		return cells;
	}

	private class SudokuWorker implements Callable<AbstractSudoku> {

		private AbstractSudoku sudoku;

		public SudokuWorker(AbstractSudoku sudoku) {
			super();
			this.sudoku = sudoku.copy();
		}

		@Override
		public AbstractSudoku call() throws Exception {
			try {
				return solve(sudoku, null);
			} finally {
				print();
				synchronized (lock) {
					lock.notify();
				}
			}
		}

	}

}
