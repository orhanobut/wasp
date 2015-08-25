package com.orhanobut.wasp;

import android.util.Log;

import com.orhanobut.wasp.utils.LogLevel;

@SuppressWarnings("unused")
public final class Logger {

  private Logger() {
    //no instance
  }

  /*Android's max limit for a log entry is ~4076 bytes,
  so 4000 bytes is used as chunk size since default charset is UTF-8*/
  private static final int CHUNK_SIZE = 4000;

  private static final String TAG = "Wasp";

  public static void d(String message) {
    log(Log.DEBUG, message);
  }

  public static void e(String message) {
    log(Log.ERROR, message);
  }

  public static void w(String message) {
    log(Log.WARN, message);
  }

  public static void i(String message) {
    log(Log.INFO, message);
  }

  public static void v(String message) {
    log(Log.VERBOSE, message);
  }

  public static void wtf(String message) {
    log(Log.ASSERT, message);
  }

  private static void log(int logType, String message) {
    LogLevel logLevel = Wasp.getLogLevel();
    if (logLevel == LogLevel.NONE) {
      return;
    }
    //get bytes of message with system's default charset (which is UTF-8 for Android)
    byte[] bytes = message.getBytes();
    int length = bytes.length;
    if (length <= CHUNK_SIZE) {
      logChunk(logType, message);
      return;
    }

    for (int i = 0; i < length; i += CHUNK_SIZE) {
      int count = Math.min(length - i, CHUNK_SIZE);
      //create a new String with system's default charset (which is UTF-8 for Android)
      logChunk(logType, new String(bytes, i, count));
    }
  }

  private static void logChunk(int logType, String chunk) {
    switch (logType) {
      case Log.ERROR:
        Log.e(TAG, chunk);
        break;
      case Log.INFO:
        Log.i(TAG, chunk);
        break;
      case Log.VERBOSE:
        Log.v(TAG, chunk);
        break;
      case Log.WARN:
        Log.w(TAG, chunk);
        break;
      case Log.ASSERT:
        Log.wtf(TAG, chunk);
        break;
      case Log.DEBUG:
        // Fall through, log debug by default
      default:
        Log.d(TAG, chunk);
        break;
    }
  }

}