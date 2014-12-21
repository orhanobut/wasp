package com.github.nr4bt.wasp;

import android.util.Log;

/**
 * @author Orhan Obut
 */
final class Logger {

    private static final String TAG = "Wasp";

    static void d(String message) {
        Log.d(TAG, message);
    }

    static void e(String message) {
        Log.e(TAG, message);
    }

    static void w(String message) {
        Log.w(TAG, message);
    }

    static void i(String message) {
        Log.i(TAG, message);
    }

    static void v(String message) {
        Log.v(TAG, message);
    }
}
