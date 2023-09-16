package com.cobelpvp.atheneum.util;

public class NumberUtils {
    public static boolean isInteger(final String s) {
        final int radix = 10;
        int result = 0;
        int i = 0;
        final int len = s.length();
        int limit = -2147483647;
        if (len > 0) {
            final char firstChar = s.charAt(0);
            if (firstChar < '0') {
                if (firstChar == '-') {
                    limit = Integer.MIN_VALUE;
                } else if (firstChar != '+') {
                    return false;
                }
                if (len == 1) {
                    return false;
                }
                ++i;
            }
            final int multmin = limit / radix;
            while (i < len) {
                final int digit = Character.digit(s.charAt(i++), radix);
                if (digit < 0) {
                    return false;
                }
                if (result < multmin) {
                    return false;
                }
                result *= radix;
                if (result < limit + digit) {
                    return false;
                }
                result -= digit;
            }
            return true;
        }
        return false;
    }

    public static boolean isShort(final String input) {
        if (!isInteger(input)) {
            return false;
        }
        final int value = Integer.parseInt(input);
        return value > -32768 && value < 32767;
    }
}
