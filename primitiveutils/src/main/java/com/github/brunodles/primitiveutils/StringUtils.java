package com.github.brunodles.primitiveutils;


/**
 * Created by bruno on 05/10/16.
 */

public final class StringUtils {
    private StringUtils() {
    }

    public static String capitalFirst(String string) {
        if (string.length() > 1)
            return string.substring(0, 1).toUpperCase() + string.substring(1);
        return string.toUpperCase();
    }

    public static String quote(String s) {
        return String.format("\"%s\"", s);
    }
}
