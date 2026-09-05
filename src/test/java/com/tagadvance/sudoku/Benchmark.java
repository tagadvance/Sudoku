package com.tagadvance.sudoku;

import static java.util.function.Predicate.not;

import com.google.common.collect.ImmutableCollection;
import com.tagadvance.geometry.Dimension;
import com.tagadvance.geometry.Point;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Measures how much work a solve costs, not just how long it takes. Counts and allocated bytes do
 * not move when the CPU throttles, so they compare across machines and across branches; the wall
 * clock at the end does not, and is labelled accordingly.
 *
 * <p>Run with {@code ./gradlew benchmark}.
 */
public final class Benchmark {

	private static final int WARMUP = 20, MEASURED = 60;

	private Benchmark() {
		super();
	}

	public static void main(final String[] args) {
		final var factory = SudokuBuilder.newBuilder().createClassicSudokuFactory();
		final var values = factory.getValues();
		final var sudoku = factory.createSudoku();
		final var template = factory.createEmptyGrid();
		final var puzzles = readPuzzles();
		final var solver = new SimpleSudokuSolver();

		System.out.printf("puzzles                    %,12d%n", puzzles.size());

		final var counters = new Counters();
		final var counting = new CountingSudoku(sudoku, counters);
		for (final var puzzle : puzzles) {
			final var grid = template.copy();
			grid.populate(puzzle, values);
			solver.solve(counting, new CountingGrid(grid, counters));
		}

		System.out.println();
		System.out.println("=== work, one pass over every puzzle (machine independent) ===");
		System.out.printf("grid copies                %,12d%n", counters.gridCopies);
		System.out.printf("candidate lookups          %,12d%n", counters.candidateLookups);
		System.out.printf("isValid checks             %,12d%n", counters.validChecks);
		System.out.printf("isSolved checks            %,12d%n", counters.solvedChecks);

		// unwrapped from here down, so the counting delegates do not distort the cost
		final var times = new ArrayList<Long>();
		long allocated = 0;
		for (int i = 0; i < WARMUP + MEASURED; i++) {
			final boolean measured = i >= WARMUP;
			final long bytesBefore = allocatedBytes();
			final long t0 = System.nanoTime();
			for (final var puzzle : puzzles) {
				final var grid = template.copy();
				grid.populate(puzzle, values);
				final var solution = solver.solve(sudoku, grid);
				if (solution == null || !sudoku.isSolved(solution)) {
					throw new IllegalStateException("a puzzle went unsolved");
				}
			}
			final long elapsed = System.nanoTime() - t0;
			if (measured) {
				times.add(elapsed);
				allocated += allocatedBytes() - bytesBefore;
			}
		}

		System.out.println();
		System.out.println("=== allocation (machine independent) ===");
		System.out.printf("bytes allocated per solve  %,12d%n",
			allocated / ((long) MEASURED * puzzles.size()));

		Collections.sort(times);
		System.out.println();
		System.out.println("=== wall clock (throttle sensitive, compare only on one machine) ===");
		System.out.printf("median ms per solve        %,12.3f%n", ms(times.get(times.size() / 2), puzzles));
		System.out.printf("best   ms per solve        %,12.3f%n", ms(times.getFirst(), puzzles));
	}

	private static double ms(final long nanos, final List<String> puzzles) {
		return nanos / 1e6 / puzzles.size();
	}

	private static long allocatedBytes() {
		final var bean = ManagementFactory.getThreadMXBean();
		if (bean instanceof final com.sun.management.ThreadMXBean sun) {
			return sun.getCurrentThreadAllocatedBytes();
		}

		return 0;
	}

	private static List<String> readPuzzles() {
		try (final var is = Benchmark.class.getResourceAsStream("/puzzles.txt");
			final var in = new InputStreamReader(is, StandardCharsets.UTF_8)) {
			return new java.io.BufferedReader(in).lines()
				.map(String::trim)
				.filter(not(line -> line.isEmpty() || line.startsWith("#")))
				.collect(Collectors.toList());
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static final class Counters {

		private long gridCopies, candidateLookups, validChecks, solvedChecks;

	}

	private record CountingSudoku(Sudoku delegate, Counters counters) implements Sudoku {

		@Override
		public Sudoku copy() {
			return this;
		}

		@Override
		public com.google.common.collect.ImmutableSet<Character> getValues() {
			return delegate.getValues();
		}

		@Override
		public com.google.common.collect.ImmutableSet<Scope> getScopes() {
			return delegate.getScopes();
		}

		@Override
		public Set<Character> getPotentialValuesForCell(final Grid grid, final Cell cell) {
			counters.candidateLookups++;

			return delegate.getPotentialValuesForCell(grid, cell);
		}

		@Override
		public boolean isValid(final Grid grid) {
			counters.validChecks++;

			return delegate.isValid(grid);
		}

		@Override
		public boolean isSolved(final Grid grid) {
			counters.solvedChecks++;

			return delegate.isSolved(grid);
		}

	}

	private record CountingGrid(Grid delegate, Counters counters) implements Grid {

		@Override
		public Grid copy() {
			counters.gridCopies++;

			return new CountingGrid(delegate.copy(), counters);
		}

		@Override
		public Dimension getSize() {
			return delegate.getSize();
		}

		@Override
		public ImmutableCollection<Cell> getCells() {
			return delegate.getCells();
		}

		@Override
		public Cell getCellAt(final Point point) {
			return delegate.getCellAt(point);
		}

	}

}
