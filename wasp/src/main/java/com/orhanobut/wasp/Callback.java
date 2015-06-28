package com.orhanobut.wasp;

/**
 * @author Orhan Obut
 */
public interface Callback<T> {

  void onSuccess(Response response, T t);

  void onError(WaspError error);
}
