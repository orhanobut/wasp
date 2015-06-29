package com.orhanobut.wasp;

/**
 * @author Orhan Obut
 */
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
