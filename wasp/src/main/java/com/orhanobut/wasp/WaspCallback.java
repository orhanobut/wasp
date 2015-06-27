package com.orhanobut.wasp;

/**
 * @author Orhan Obut
 */
public interface WaspCallback<T> {

  void onSuccess(T t);

  void onError(WaspError error);
}
