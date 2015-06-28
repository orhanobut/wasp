package com.orhanobut.wasp;

/**
 * @author Orhan Obut
 */
public interface WaspRequest {

  boolean isCancelled();

  void cancel();
}