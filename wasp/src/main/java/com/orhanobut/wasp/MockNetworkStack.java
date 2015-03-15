package com.orhanobut.wasp;

import android.content.Context;
import android.text.TextUtils;

import com.orhanobut.wasp.utils.MockFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;

/**
 * A NetworkStack implementation for delivering offline mock responses.
 *
 * @author Orhan Obut
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
    public <T> void invokeRequest(WaspRequest waspRequest, CallBack<T> callBack) {
        WaspMock mock = waspRequest.getMock();
        int statusCode = mock.getStatusCode();

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

        WaspResponse waspResponse = new WaspResponse.Builder()
                .setUrl(waspRequest.getUrl())
                .setStatusCode(statusCode)
                .setHeaders(Collections.<String, String>emptyMap())
                .setBody(responseString)
                .setResponseObject(responseObject)
                .setLength(responseString.length())
                .setNetworkTime(0)
                .build();

        if (statusCode < 200 || statusCode > 299) {
            callBack.onError(new WaspError(waspResponse, "Mock error message!"));
            return;
        }

        callBack.onSuccess((T) waspResponse);
    }
}
