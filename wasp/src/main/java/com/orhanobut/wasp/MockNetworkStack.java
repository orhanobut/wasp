package com.orhanobut.wasp;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.orhanobut.wasp.utils.MockFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;

/**
 * A NetworkStack implementation for delivering offline mock responses.
 */
class MockNetworkStack implements NetworkStack {

  private static MockNetworkStack mockNetworkStack;

  private Context context;

  private MockNetworkStack(Context context) {
    this.context = context;
  }

  static MockNetworkStack getDefault(Context context) {
    if (mockNetworkStack == null) {
      mockNetworkStack = new MockNetworkStack(context);
    }
    return mockNetworkStack;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void invokeRequest(RequestCreator waspRequest,
                            final InternalCallback<Response> waspCallback) {
    MockHolder mock = waspRequest.getMock();
    final int statusCode = mock.getStatusCode();

    MethodInfo methodInfo = waspRequest.getMethodInfo();
    Type responseType = methodInfo.getResponseObjectType();

    String responseString;
    Object responseObject;

    if (TextUtils.isEmpty(mock.getPath())) {
      //Create mock object and return
      responseObject = MockFactory.createMockObject(responseType);
      responseString = Wasp.getParser().toBody(responseObject);
    } else {
      responseString = MockFactory.readMockResponse(context, mock.getPath());
      try {
        responseObject = Wasp.getParser().fromBody(responseString, responseType);
      } catch (IOException e) {
        throw new RuntimeException("Mock file \"" + mock.getPath()
            + "\" is in an invalid format", e);
      }
    }

    final Response waspResponse = new Response.Builder()
        .setUrl(waspRequest.getUrl())
        .setStatusCode(statusCode)
        .setHeaders(Collections.<String, String>emptyMap())
        .setBody(responseString)
        .setResponseObject(responseObject)
        .setLength(responseString.length())
        .setNetworkTime(1000)
        .build();

    //delay the response 1 second
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        if (statusCode < 200 || statusCode > 299) {
          waspCallback.onError(new WaspError(waspResponse, "Mock error message!"));
          return;
        }

        waspCallback.onSuccess(waspResponse);
      }
    }, 1000);

  }

  @Override
  public Object invokeRequest(RequestCreator requestCreator) {
    MockHolder mock = requestCreator.getMock();
    final int statusCode = mock.getStatusCode();

    MethodInfo methodInfo = requestCreator.getMethodInfo();
    Type responseType = methodInfo.getResponseObjectType();

    String responseString;
    Object responseObject;

    if (TextUtils.isEmpty(mock.getPath())) {
      //Create mock object and return
      responseObject = MockFactory.createMockObject(responseType);
      responseString = Wasp.getParser().toBody(responseObject);
    } else {
      responseString = MockFactory.readMockResponse(context, mock.getPath());
      try {
        responseObject = Wasp.getParser().fromBody(responseString, responseType);
      } catch (IOException e) {
        throw new RuntimeException("Mock file \"" + mock.getPath()
            + "\" is in an invalid format", e);
      }
    }

    final Response waspResponse = new Response.Builder()
        .setUrl(requestCreator.getUrl())
        .setStatusCode(statusCode)
        .setHeaders(Collections.<String, String>emptyMap())
        .setBody(responseString)
        .setResponseObject(responseObject)
        .setLength(responseString.length())
        .setNetworkTime(1000)
        .build();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Logger.e(e.getMessage());
    }

    return waspResponse.getResponseObject();
  }
}
