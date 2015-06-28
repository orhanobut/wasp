package com.orhanobut.wasp;

/**
 * @author Orhan Obut
 */
interface NetworkStack {

  <T> void invokeRequest(WaspRequest waspRequest, WaspCallback<T> waspCallback);
}
