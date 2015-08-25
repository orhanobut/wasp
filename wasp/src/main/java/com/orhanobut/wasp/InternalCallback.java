package com.orhanobut.wasp;

public interface InternalCallback<T> {

  void onSuccess(T t);

  void onError(WaspError error);
}
