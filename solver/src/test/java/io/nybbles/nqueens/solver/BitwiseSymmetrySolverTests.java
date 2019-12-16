package io.nybbles.nqueens.solver;

import org.junit.Test;

import static org.junit.Assert.*;

public class BitwiseSymmetrySolverTests {
    @Test
    public void solveWhereN4() {
        var context = new Context(4);
        var solver = new BitwiseSymmetrySolver(context);
        var solutions = solver.solve();
        assertEquals(1, solutions.size());
        var s = solutions.get(0);
        assertEquals(s[0], 0b0100);
        assertEquals(s[1], 0b0001);
        assertEquals(s[2], 0b1000);
        assertEquals(s[3], 0b0010);
    }

    @Test
    public void solveWhereN5() {
        var context = new Context(5);
        var solver = new BitwiseSymmetrySolver(context);
        var solutions = solver.solve();
        assertEquals(5, solutions.size());
        var s = solutions.get(0);
        assertEquals(s[0], 0b00100);
        assertEquals(s[1], 0b10000);
        assertEquals(s[2], 0b00010);
        assertEquals(s[3], 0b01000);
        assertEquals(s[4], 0b00001);
    }

}
