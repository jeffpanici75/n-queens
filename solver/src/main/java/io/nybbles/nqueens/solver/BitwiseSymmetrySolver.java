package io.nybbles.nqueens.solver;

import java.util.ArrayList;
import java.util.Stack;

//
// This algorithm is based on the paper,
// "Backtracking Algorithms in MCPL using Bit Patterns and Recursion," by
// Martin Richards.
//
// https://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.51.7113&rep=rep1&type=pdf
//
// I use an iterative state machine approach instead of recursion. Additionally,
// my algorithm handles "mirror" solutions by trivially excluding them.  It is
// easy to bit reverse each row to recreate the mirror solutions at a later time.
//
public class BitwiseSymmetrySolver {
    private Context _context;

    private enum State {
        BEGIN,
        ITERATE,
        BACKTRACK
    }

    private static final class Entry {
        public Entry(
                int leftDiagonal,
                int workingRow,
                int rightDiagonal,
                int exclusionMask1,
                int exclusionMask2,
                int queenPlacement) {
            this.workingRow = workingRow;
            this.leftDiagonal = leftDiagonal;
            this.rightDiagonal = rightDiagonal;
            this.exclusionMask1 = exclusionMask1;
            this.exclusionMask2 = exclusionMask2;
            this.queenPlacement = queenPlacement;
        }
        public int workingRow;
        public int leftDiagonal;
        public int rightDiagonal;
        public int queenPlacement;
        public int exclusionMask1;
        public int exclusionMask2;
        public int possibleAttacks;
    }

    public BitwiseSymmetrySolver(Context context) {
        _context = context;
    }

    public ArrayList<int[]> solve() {
        var solutions = new ArrayList<int[]>();
        var state = State.BEGIN;

        var rowStack = new Stack<Entry>();
        rowStack.ensureCapacity(_context.n + 1);

        var e = new Entry(
                0,
                0,
                0,
                _context.exclusions,
                (_context.n % 2) != 0 ? _context.exclusions : 0,
                0);

        do {
            switch (state) {
                case BEGIN -> {
                    if (e.workingRow == _context.done) {
                        rowStack.push(e);
                        _context.count++;
                        var rows = new int[rowStack.size() - 1];
                        for (var i = 0; i < rowStack.size() - 1; ++i)
                            rows[i] = rowStack.get(i + 1).queenPlacement;
                        solutions.add(rows);
                        state = State.BACKTRACK;
                    } else {
                        var conflict = e.leftDiagonal | e.workingRow | e.rightDiagonal | e.exclusionMask1;
                        e.possibleAttacks = ~conflict & _context.done;
                        rowStack.push(e);
                        state = State.ITERATE;
                    }
                }
                case ITERATE -> {
                    if (e.possibleAttacks != 0) {
                        var queenPlacement = e.possibleAttacks & -e.possibleAttacks;
                        e.possibleAttacks = e.possibleAttacks ^ queenPlacement;
                        e = new Entry(
                                (e.leftDiagonal | queenPlacement) >> 1,
                                e.workingRow | queenPlacement,
                                (e.rightDiagonal | queenPlacement) << 1,
                                e.exclusionMask2,
                                0,
                                queenPlacement);
                        state = State.BEGIN;
                    } else {
                        state = State.BACKTRACK;
                    }
                }
                case BACKTRACK -> {
                    rowStack.pop();
                    if (!rowStack.isEmpty()) {
                        e = rowStack.peek();
                        e.exclusionMask2 = 0;
                        state = State.ITERATE;
                    }
                }
            }
        } while (!rowStack.isEmpty());

        return solutions;
    }
}
