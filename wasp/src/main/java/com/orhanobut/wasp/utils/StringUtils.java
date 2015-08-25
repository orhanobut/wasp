package com.orhanobut.wasp.utils;

public class StringUtils {

  private StringUtils() {
    // no instance
  }

  public static String wrap(String value, String delimiter) {
    return delimiter + value + delimiter;
  }

  /**
   * Creates a cache key for use with the L1 cache.
   *
   * @param url       The URL of the request.
   * @param maxWidth  The max-width of the output.
   * @param maxHeight The max-height of the output.
   */
  public static String getCacheKey(String url, int maxWidth, int maxHeight) {
    return "#W" + maxWidth + "#H" + maxHeight + url;
  }

}
