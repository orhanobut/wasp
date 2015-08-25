package com.orhanobut.wasp;

public interface WaspRequest {

  boolean isCancelled();

  void cancel();
}