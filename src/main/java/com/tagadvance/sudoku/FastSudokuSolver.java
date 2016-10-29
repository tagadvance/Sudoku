package com.tagadvance.sudoku;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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

public class FastSudokuSolver implements SudokuSolver {

	private ExecutorService threadPool;
	private final Object lock = new Object();
	private volatile boolean shutdown;
	private volatile int callCount;
	private long start;
	private List<Future<Sudoku>> futureList;

	public FastSudokuSolver() {
		super();
	}
	
	@Override
	public Sudoku solve(Sudoku sudoku) throws UnsolvableException {
		Preconditions.checkNotNull(sudoku, "sudoku must not be null");
		sudoku = sudoku.copy();

		sudoku.validate();

		List<SudokuWorker> callList = fork(sudoku);

		int callSize = callList.size();
		System.out.println("callList.size " + callSize);

		Runtime runtime = Runtime.getRuntime();
		int cores = runtime.availableProcessors();
		threadPool = Executors.newFixedThreadPool(cores);
		shutdown = false;

		callCount = 0;
		start = System.currentTimeMillis();

		this.futureList = new ArrayList<Future<Sudoku>>();
		for (Callable<Sudoku> call : callList) {
			Future<Sudoku> future = threadPool.submit(call);
			futureList.add(future);
		}

		threadPool.shutdown();
		return join();
	}

	private List<SudokuWorker> fork(Sudoku sudoku) {
		List<SudokuWorker> callList = new ArrayList<SudokuWorker>();
		int depth = calculateDepth();
		fork(sudoku, callList, depth);
		return callList;
	}

	private int calculateDepth() {
		return 4;
	}

	private void fork(Sudoku sudoku, List<SudokuWorker> callList, int depth) {
		if (depth <= 0) {
			callList.add(new SudokuWorker(sudoku));
			return;
		}

		List<Point> priorityList = getPriorityList(sudoku);
		Point pt = priorityList.get(0);

		Set<String> potentialCellValues = sudoku.getCellPotentialValues(pt.x, pt.y);
		for (String value : potentialCellValues) {
			sudoku.setCellValue(pt.x, pt.y, value);
			fork(sudoku, callList, depth - 1);
		}

		sudoku.setCellValue(pt.x, pt.y, Sudoku.EMPTY);
	}

	private Sudoku join() {
		while (!threadPool.isTerminated()) {
			for (Iterator<Future<Sudoku>> i = futureList.iterator(); i.hasNext();) {
				Future<Sudoku> future = i.next();
				if (!future.isDone()) {
					continue;
				}

				i.remove();

				Sudoku result = null;
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

	private Sudoku solve(Sudoku sudoku, List<Point> cells) {
		if (shutdown) {
			return null;
		}
		if (sudoku.isSolved()) {
			return sudoku;
		}
		if (futureList.size() == 1) {
			try {
				return solve(sudoku);
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
			Sudoku result = solve(sudoku, null);
			if (result != null) {
				return result;
			}
		}

		sudoku.setCellValue(p.x, p.y, Sudoku.EMPTY);
		cells.add(0, p);

		return null;
	}

	private List<Point> getPriorityList(Sudoku sudoku) {
		List<Point> emptyCells = sudoku.getEmptyCells();
		prioritize(sudoku, emptyCells);
		return emptyCells;
	}

	private Map<Point, Integer> prioritize(Sudoku sudoku, List<Point> emptyCells) {
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

	private class SudokuWorker implements Callable<Sudoku> {

		private Sudoku sudoku;

		public SudokuWorker(Sudoku sudoku) {
			super();
			this.sudoku = sudoku.copy();
		}

		@Override
		public Sudoku call() throws Exception {
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
