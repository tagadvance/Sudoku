# Sudoku
Sudoku solving algorithm written in Java.

This is a sudoku solver I originally wrote in April of 2012 (based on the last modified date). I'll be refactoring it over the next few days to improve the quality of code.

## Algorithm
The algorithm works roughly like this:

1. If the sudoku is solved, return immediately.
1. Ensure that the sudoku is in a valid state.
2. Identify cells with 1 possible answer and insert the correct value.
3. Sort remaining cells in order of fewest number of possible values to greatest number of possible values.
4. Recursively iterate over each cell, repeating the above steps, until a solution is found.

The actual implementation is a bit more complicated that that because an [ExecutorService](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html) is used to increase performance.