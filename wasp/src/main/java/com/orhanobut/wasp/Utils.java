package com.orhanobut.wasp;

final class Utils {

  private Utils() {
    //no instance
  }

  static boolean hasRxJavaOnClasspath() {
    try {
      Class.forName("rx.Observable");
      return true;
    } catch (ClassNotFoundException ignored) {
      Logger.i("rx.Observable not found");
    }
    return false;
  }

  public static void checkRx() {
    if (!hasRxJavaOnClasspath()) {
      throw new NoClassDefFoundError("RxJava is not on classpath, "
          + "make sure that you have it in your dependencies");
    }
  }

}