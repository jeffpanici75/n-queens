package io.nybbles.common;

public final class BitHelpers {
    public static int bitReverse(int x, int bits) {
        x = ((x & 0x55555555) << 1) | ((x & 0xAAAAAAAA) >> 1);
        x = ((x & 0x33333333) << 2) | ((x & 0xCCCCCCCC) >> 2);
        x = ((x & 0x0F0F0F0F) << 4) | ((x & 0xF0F0F0F0) >> 4);
        x = ((x & 0x00FF00FF) << 8) | ((x & 0xFF00FF00) >> 8);
        x = ((x & 0x0000FFFF) << 16) | ((x & 0xFFFF0000) >> 16);
        return x >> (32 - bits);
    }

    public static String binaryStringForSize(long value, int size) {
        var buffer = new char[size];
        for (int i = 0; i < size; i++) {
            var mask = 1L << i;
            if ((value & mask) == mask) {
                buffer[(size - 1) - i] = '1';
            } else {
                buffer[(size - 1) - i] = '0';
            }
        }
        return new String(buffer);
    }
}
