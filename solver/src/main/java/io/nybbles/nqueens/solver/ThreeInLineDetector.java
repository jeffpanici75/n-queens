package io.nybbles.nqueens.solver;

import io.nybbles.common.CombinationKofNIterator;
import io.nybbles.nqueens.Constants;
import io.nybbles.common.MathHelpers;

//
// Algorithm based on the "No-three-in-line problem".
// https://en.wikipedia.org/wiki/No-three-in-line_problem
//
public class ThreeInLineDetector {
    private static final int K = 3;

    private interface DetectorAction {
        void execute(boolean areCollinear, int count, byte[] queens);
    }

    public static class Result {
        public Result(int size) {
            testLines = new byte[size][3];
            straightLines = new byte[size][3];
        }
        public int testLineCount;
        public byte[][] testLines;
        public int straightLineCount;
        public byte[][] straightLines;
    }

    private final Queen[] _queens = new Queen[Constants.MAXIMUM_N];
    private final byte[] _ids = new byte[3];

    private boolean areCollinear(
            int n,
            DetectorAction action) {
        var permutations = new CombinationKofNIterator<Queen>(_queens, K, n);
        var collinear = false;
        var count = 0;
        while (permutations.hasNext()) {
            var permutation = permutations.next();

            var q1 = permutation.get(0);
            var q2 = permutation.get(1);
            var q3 = permutation.get(2);

            double y1 = q2.row - q1.row;
            double x1 = q2.column - q1.column;
            var s1 = y1 / x1;

            double y2 = q3.row - q1.row;
            double x2 = q3.column - q1.column;
            var s2 = y2 / x2;

            var result = s1 == s2;
            if (result) {
                collinear = true;
                if (action == null)
                    break;
            }

            if (action != null) {
                _ids[0] = q1.id;
                _ids[1] = q2.id;
                _ids[2] = q3.id;
                action.execute(result, count, _ids);
            }
            ++count;
        }
        return collinear;
    }

    public ThreeInLineDetector() {
    }

    public Queen[] getQueens() {
        return _queens;
    }

    public boolean areCollinear(int n) {
        return areCollinear(n, null);
    }

    public Result areCollinearWithResult(int n) {
        var combinationsCount = MathHelpers.combinationsKofN(K, n);
        final var result = new Result((int) combinationsCount);

        areCollinear(
                n,
                (collinear, count, ids) -> {
                    if (collinear) {
                        result.straightLines[result.straightLineCount][0] = ids[0];
                        result.straightLines[result.straightLineCount][1] = ids[1];
                        result.straightLines[result.straightLineCount][2] = ids[2];
                        ++result.straightLineCount;
                    }
                    result.testLines[result.testLineCount][0] = ids[0];
                    result.testLines[result.testLineCount][1] = ids[1];
                    result.testLines[result.testLineCount][2] = ids[2];
                    ++result.testLineCount;
                });

        return result;
    }
}
