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
        Request request = new StringRequest(
                method,
                url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String s) {
                        callBack.onSuccess(s);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        callBack.onError(new WaspError(
                                url,
                                volleyError.getMessage(),
                                volleyError.networkResponse.statusCode
                        ));
                    }
                }
        ) {

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
            case "GET":
                return Request.Method.GET;
            case "POST":
                return Request.Method.POST;
            case "PUT":
                return Request.Method.PUT;
            case "DELETE":
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
}
