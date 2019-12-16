package io.nybbles.nqueens.solver;

public class Context {
    public Context(int n) {
        this.n = n;
        this.done = (1 << n) - 1;
        this.exclusions = (1 << (n / 2)) - 1;
    }

    public int count;
    public final int n;
    public final int done;
    public final int exclusions;
}
