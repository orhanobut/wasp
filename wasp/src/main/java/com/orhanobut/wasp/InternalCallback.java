package com.orhanobut.wasp;

/**
 * @author Orhan Obut
 */
public interface InternalCallback<T> {

  void onSuccess(T t);

  void onError(WaspError error);
}
