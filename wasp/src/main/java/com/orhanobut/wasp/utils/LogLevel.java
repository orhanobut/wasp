package com.orhanobut.wasp.utils;

public enum LogLevel {
  /**
   * No logs are printed
   */
  NONE,

  /**
   * Print logs both for REST and Images request
   */
  FULL,

  /**
   * Print logs for REST
   */
  FULL_REST_ONLY,

  /**
   * Print logs for Images
   */
  FULL_IMAGE_ONLY
}
