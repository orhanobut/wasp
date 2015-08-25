package com.orhanobut.wasp;

public interface Callback<T> {

  void onSuccess(Response response, T t);

  void onError(WaspError error);
}
