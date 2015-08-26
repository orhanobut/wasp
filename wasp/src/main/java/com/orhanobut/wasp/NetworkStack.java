package com.orhanobut.wasp;

public interface NetworkStack {

  void invokeRequest(RequestCreator requestCreator, InternalCallback<Response> waspCallback);

  Object invokeRequest(RequestCreator requestCreator) throws Exception;
}
