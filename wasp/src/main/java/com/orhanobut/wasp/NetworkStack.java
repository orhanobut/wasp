package com.orhanobut.wasp;

/**
 * @author Orhan Obut
 */
interface NetworkStack {

  <T> void invokeRequest(RequestCreator requestCreator, InternalCallback<T> waspCallback);

  <T> T invokeRequest(RequestCreator requestCreator) throws Exception;
}
