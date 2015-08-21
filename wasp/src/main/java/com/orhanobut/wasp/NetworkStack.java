package com.orhanobut.wasp;

/**
 * @author Orhan Obut
 */
interface NetworkStack {

  void invokeRequest(RequestCreator requestCreator, InternalCallback<Response> waspCallback);

  Object invokeRequest(RequestCreator requestCreator) throws Exception;
}
