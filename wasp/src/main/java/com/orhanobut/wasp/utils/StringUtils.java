package com.orhanobut.wasp.utils;

/**
 * @author Orhan Obut
 */
class StringUtils {

    private StringUtils() {
        // no instance
    }

    public static String wrap(String value, String delimiter) {
        return delimiter + value + delimiter;
    }

}
