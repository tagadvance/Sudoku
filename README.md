# Sudoku

[![CI](https://github.com/tagadvance/Sudoku/actions/workflows/ci.yml/badge.svg)](https://github.com/tagadvance/Sudoku/actions/workflows/ci.yml)
[![License](https://img.shields.io/github/license/tagadvance/Sudoku)](LICENSE)
[![Java](https://img.shields.io/badge/Java-25%2B-007396?logo=openjdk&logoColor=white)](https://adoptium.net)

Sudoku solving algorithm written in Java.

This is a sudoku solver I originally wrote in April of 2012 (based on the last modified date).

## Algorithm
The algorithm works roughly like this:

1. Make a copy of the grid, so the caller's puzzle is left alone.
1. If any scope already holds a duplicate, the puzzle is unsolvable; return `null`.
1. Work out which values each empty cell could still take, and pick the cell with the fewest.
1. Assign that cell's candidates one at a time. Return immediately if the puzzle is solved, otherwise recurse to step 3.
1. If no candidate leads to a solution, clear the cell and backtrack.

---

If you find this useful, you can [sponsor me on GitHub](https://github.com/sponsors/tagadvance).
