package com.github.nr4bt.wasp;

/**
 * @author Orhan Obut
 */
interface NetworkStack {

    <T> void invokeRequest(WaspRequest waspRequest, CallBack<T> callBack);
}
