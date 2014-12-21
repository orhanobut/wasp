package com.github.nr4bt.wasp;

/**
 * @author Orhan Obut
 */
public interface CallBack<T> {

    void onSuccess(T t);

    void onError(WaspError error);
}
