package com.orhanobut.wasp;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;
import com.orhanobut.wasp.utils.WaspHttpStack;
import com.orhanobut.wasp.utils.WaspRetryPolicy;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Orhan Obut
 */
final class VolleyNetworkStack implements NetworkStack {

    private static final String METHOD_GET = "GET";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_DELETE = "DELETE";

    private static RequestQueue requestQueue;

    private VolleyNetworkStack(Context context, WaspHttpStack stack) {
        requestQueue = Volley.newRequestQueue(context, (HttpStack) stack.getHttpStack());
        //requestQueue = Volley.newRequestQueue(context);
    }

    static VolleyNetworkStack newInstance(Context context, WaspHttpStack stack) {
        return new VolleyNetworkStack(context, stack);
    }

    static RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            throw new NullPointerException("Wasp.Builder must be called");
        }
        return requestQueue;
    }

    private void addToQueue(final WaspRequest waspRequest, final CallBack callBack) {
        String url = waspRequest.getUrl();
        int method = getMethod(waspRequest.getMethod());
        VolleyListener listener = VolleyListener.newInstance(callBack, url);
        Request request = new VolleyRequest(method, url, waspRequest.getBody(), listener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return waspRequest.getHeaders();
            }
        };

        WaspRetryPolicy policy = waspRequest.getRetryPolicy();
        if (policy != null) {
            request.setRetryPolicy(policy);
        }

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

    private static class VolleyListener<T> implements
            Response.Listener<T>,
            Response.ErrorListener {

        private final CallBack callBack;
        private final String url;

        private VolleyListener(CallBack callBack, String url) {
            this.callBack = callBack;
            this.url = url;
        }

        public static VolleyListener newInstance(CallBack callBack, String url) {
            return new VolleyListener(callBack, url);
        }

        @Override
        public void onResponse(T response) {
            callBack.onSuccess(response);
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            int statusCode = WaspError.INVALID_STATUS_CODE;
            byte[] body = new byte[0];
            Map<String, String> headers = new HashMap<>();
            long delay = 0;
            if (error == null) {
                callBack.onError(new WaspError(url, statusCode, headers, "No message", body, delay));
                return;
            }
            if (error.networkResponse != null) {
                statusCode = error.networkResponse.statusCode;
                headers = error.networkResponse.headers;
                body = error.networkResponse.data;
            }
            callBack.onError(new WaspError(
                    url,
                    statusCode,
                    headers,
                    error.getMessage(),
                    body,
                    delay
            ));
        }
    }

    private static class VolleyRequest<T> extends Request<T> {

        /**
         * Charset for request.
         */
        private static final String PROTOCOL_CHARSET = "utf-8";

        /**
         * Content type for request.
         */
        private static final String PROTOCOL_CONTENT_TYPE =
                String.format("application/json; charset=%s", PROTOCOL_CHARSET);

        private final VolleyListener<T> listener;
        private final String requestBody;
        private final String url;

        public VolleyRequest(int method, String url, String requestBody, VolleyListener<T> listener) {
            super(method, url, listener);
            this.url = url;
            this.listener = listener;
            this.requestBody = requestBody;
        }

        @Override
        protected void deliverResponse(T response) {
            listener.onResponse(response);
        }

        @Override
        protected Response parseNetworkResponse(NetworkResponse response) {
            try {
                byte[] data = response.data;
                String body = new String(data, HttpHeaderParser.parseCharset(response.headers));
                int length = data.length;
                long delay = response.networkTimeMs;
                WaspResponse waspResponse = new WaspResponse(
                        url, response.statusCode, response.headers, body, length, delay
                );
                return Response.success(waspResponse, HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            }
        }

        @Override
        public String getBodyContentType() {
            return PROTOCOL_CONTENT_TYPE;
        }

        @Override
        public byte[] getBody() {
            try {
                return requestBody == null ? null : requestBody.getBytes(PROTOCOL_CHARSET);
            } catch (UnsupportedEncodingException uee) {
                Logger.wtf("Unsupported Encoding while trying to get the bytes of %s using %s"
                                + requestBody
                                + PROTOCOL_CHARSET
                );
                return null;
            }
        }
    }

}
