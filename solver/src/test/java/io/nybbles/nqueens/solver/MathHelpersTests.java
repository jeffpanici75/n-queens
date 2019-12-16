package io.nybbles.nqueens.solver;

import io.nybbles.common.MathHelpers;
import org.junit.Test;

import static org.junit.Assert.*;

public class MathHelpersTests {
    @Test
    public void factorialTest() {
        var k = MathHelpers.factorial(3);
        var n = MathHelpers.factorial(17);
        assertEquals(6, k);
        assertEquals(355687428096000L, n);
    }

    @Test
    public void combinationsCountKofN() {
        assertEquals(4, MathHelpers.combinationsKofN(3, 4));
        assertEquals(56, MathHelpers.combinationsKofN(3, 8));
        assertEquals(680, MathHelpers.combinationsKofN(3, 17));
    }
}
