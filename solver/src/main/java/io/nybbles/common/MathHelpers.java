package io.nybbles.common;

public final class MathHelpers {
    public static long factorial(long n) {
        long f = 1;
        for (var i = 1; i <= n; ++i)
            f *= i;
        return f;
    }

    public static long combinationsKofN(long k, long n) {
        var factorialN = factorial(n);
        var factorialK = factorial(k);
        var factorialDiffNK = factorial(n - k);
        return factorialN / (factorialK * factorialDiffNK);
    }
}
