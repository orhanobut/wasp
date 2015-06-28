package com.orhanobut.wasp;

/**
 * @author Orhan Obut
 */
interface NetworkStack {

  <T> void invokeRequest(RequestCreator waspRequest, InternalCallback<T> waspCallback);
}
