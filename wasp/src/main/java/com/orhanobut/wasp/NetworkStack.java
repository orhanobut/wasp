package com.orhanobut.wasp;

interface NetworkStack {

  void invokeRequest(RequestCreator requestCreator, InternalCallback<Response> waspCallback);

  Object invokeRequest(RequestCreator requestCreator) throws Exception;
}
