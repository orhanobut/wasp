package com.orhanobut.wasp;

public class InternalWaspRequest implements WaspRequest {

  private boolean cancelled;

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void cancel() {
    cancelled = true;
  }

}
