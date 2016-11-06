# Sudoku
Sudoku solving algorithm written in Java.

This is a sudoku solver I originally wrote in April of 2012 (based on the last modified date).

## Algorithm
The algorithm works roughly like this:

1. Make a copy of the sudoku.
1. If the sudoku is in an invalid state, throw an UnsolvableException.
1. If the sudoku is solved, return the solved puzzle immediately.
1. Retrieve and sort empty cells in order of fewest number of possible values to greatest number of possible values.
1. For each cell, iterate over the possible values and assign the value to the cell.
1. After a value is assigned, recurse to step 1, until a solution is found.
1. If no solution is found, throw an UnsolvableException.