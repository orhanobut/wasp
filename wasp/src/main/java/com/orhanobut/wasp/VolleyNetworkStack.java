package com.orhanobut.wasp;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

/**
 * @author Orhan Obut
 */
final class VolleyNetworkStack implements NetworkStack {

    private static final String METHOD_GET = "GET";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_DELETE = "DELETE";

    private final RequestQueue requestQueue;

    private VolleyNetworkStack(Context context, HttpStack stack) {
        requestQueue = Volley.newRequestQueue(context, stack);
    }

    static VolleyNetworkStack newInstance(Context context, HttpStack stack) {
        return new VolleyNetworkStack(context, stack);
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            throw new NullPointerException("RequestQueue may not be null");
        }
        return requestQueue;
    }

    private void addToQueue(final WaspRequest waspRequest, final CallBack callBack) {
        final String url = waspRequest.getUrl();
        int method = getMethod(waspRequest.getMethod());
        VolleyResponse response = VolleyResponse.newInstance(callBack, url);
        Request request = new StringRequest(method, url, response, response) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return waspRequest.getHeaders();
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return waspRequest.getBodyAsBytes();
            }
        };

        addToQueue(request);
    }

    private int getMethod(String method) {
        switch (method) {
            case METHOD_GET:
                return Request.Method.GET;
            case METHOD_POST:
                return Request.Method.POST;
            case METHOD_PUT:
                return Request.Method.PUT;
            case METHOD_DELETE:
                return Request.Method.DELETE;
            default:
                throw new IllegalArgumentException("Method must be DELETE,POST,PUT or GET");
        }
    }

    private <T> void addToQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

    @Override
    public <T> void invokeRequest(WaspRequest waspRequest, CallBack<T> callBack) {
        addToQueue(waspRequest, callBack);
    }

    private static class VolleyResponse<T> implements
            Response.Listener<T>,
            Response.ErrorListener {

        private final CallBack callBack;
        private final String url;

        private VolleyResponse(CallBack callBack, String url) {
            this.callBack = callBack;
            this.url = url;
        }

        public static VolleyResponse newInstance(CallBack callBack, String url) {
            return new VolleyResponse(callBack, url);
        }

        @Override
        public void onResponse(T response) {
            callBack.onSuccess(response);
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            int statusCode = WaspError.INVALID_STATUS_CODE;
            if (error == null) {
                callBack.onError(new WaspError(url, "No message", statusCode));
                return;
            }
            if (error.networkResponse != null) {
                statusCode = error.networkResponse.statusCode;
            }
            callBack.onError(new WaspError(
                    url,
                    error.getMessage(),
                    statusCode
            ));
        }
    }

}
