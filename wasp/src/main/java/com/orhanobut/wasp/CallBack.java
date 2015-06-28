package com.orhanobut.wasp;

import com.android.volley.Request;

/**
 * @author Orhan Obut
 */
public interface CallBack<T> {

    void onStart(Request<T> request);

    void onSuccess(T t);

    void onError(WaspError error);
}
