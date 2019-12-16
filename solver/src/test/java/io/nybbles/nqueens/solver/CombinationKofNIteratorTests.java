package io.nybbles.nqueens.solver;

import io.nybbles.common.MathHelpers;
import io.nybbles.common.CombinationKofNIterator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CombinationKofNIteratorTests {
    @Test
    public void threePermuteOfFourInteger() {
        var values = new Integer[] {10, 3, -1, 4};
        var permutations = new CombinationKofNIterator<Integer>(
                values,
                3,
                values.length);
        var count = 0;
        while (permutations.hasNext()) {
            var permutation = permutations.next();
            assertEquals(3, permutation.size());
            ++count;
        }
        assertEquals(MathHelpers.combinationsKofN(3, 4), count);
    }

    @Test
    public void threePermuteOfEightInteger() {
        var values = new Integer[] {10, 3, -1, 4, 88, 118, 6, 2};
        var permutations = new CombinationKofNIterator<Integer>(
                values,
                3,
                values.length);
        var count = 0;
        while (permutations.hasNext()) {
            var permutation = permutations.next();
            assertEquals(3, permutation.size());
            ++count;
        }
        assertEquals(MathHelpers.combinationsKofN(3, 8), count);
    }
}
