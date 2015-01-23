package com.orhanobut.wasp;

/**
 * @author Orhan Obut
 */
public interface NetworkStack {

    <T> void invokeRequest(WaspRequest waspRequest, CallBack<T> callBack);

    public void cancelRequest(String tag);

}
