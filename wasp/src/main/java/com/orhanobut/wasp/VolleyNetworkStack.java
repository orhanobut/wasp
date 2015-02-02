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
import com.orhanobut.wasp.parsers.Parser;
import com.orhanobut.wasp.utils.WaspHttpStack;
import com.orhanobut.wasp.utils.WaspRetryPolicy;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

/**
 * @author Orhan Obut
 */
final class VolleyNetworkStack implements NetworkStack {

    private static final String METHOD_GET = "GET";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_DELETE = "DELETE";

    private RequestQueue requestQueue;

    private VolleyNetworkStack(Context context, WaspHttpStack stack) {
        requestQueue = Volley.newRequestQueue(context, (HttpStack) stack.getHttpStack());
        // requestQueue = Volley.newRequestQueue(context);
    }

    static VolleyNetworkStack newInstance(Context context, WaspHttpStack stack) {
        return new VolleyNetworkStack(context, stack);
    }

    synchronized RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            throw new NullPointerException("Wasp.Builder must be called");
        }
        return requestQueue;
    }

    private void addToQueue(final WaspRequest waspRequest, CallBack callBack, Parser parser) {
        String url = waspRequest.getUrl();
        int method = getMethod(waspRequest.getMethod());
        VolleyListener listener = VolleyListener.newInstance(callBack, url, parser);
        Request request = new VolleyRequest(method, url, waspRequest, listener) {
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
    public <T> void invokeRequest(WaspRequest waspRequest, CallBack<T> callBack, Parser parser) {
        addToQueue(waspRequest, callBack, parser);
    }

    private static class VolleyListener<T> implements
            Response.Listener<T>,
            Response.ErrorListener {

        private final CallBack callBack;
        private final String url;
        private final Parser parser;

        private VolleyListener(CallBack callBack, String url, Parser parser) {
            this.callBack = callBack;
            this.url = url;
            this.parser = parser;
        }

        public static VolleyListener newInstance(CallBack callBack, String url, Parser parser) {
            return new VolleyListener(callBack, url, parser);
        }

        @Override
        public void onResponse(T response) {
            callBack.onSuccess(response);
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            WaspResponse.Builder builder = new WaspResponse.Builder();
            String errorMessage = null;

            if (error == null) {
                builder.setUrl(url)
                        .setStatusCode(WaspError.INVALID_STATUS_CODE)
                        .setHeaders(Collections.<String, String>emptyMap())
                        .setBody(null)
                        .setLength(0)
                        .setNetworkTime(0)
                        .build();
                errorMessage = "No message";
            }

            if (error != null && error.networkResponse != null) {
                NetworkResponse response = error.networkResponse;
                String body;
                try {
                    body = new String(error.networkResponse.data, HttpHeaderParser.parseCharset(response.headers));
                } catch (UnsupportedEncodingException e) {
                    body = "Unable to parse error body!!!!!";
                }
                builder.setUrl(url)
                        .setStatusCode(response.statusCode)
                        .setHeaders(response.headers)
                        .setBody(body)
                        .setLength(response.data.length)
                        .setNetworkTime(0)
                        .build();
                errorMessage = error.getMessage();
            }

            callBack.onError(new WaspError(parser, builder.build(), errorMessage));
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
        private final Type responseObjectType;

        public VolleyRequest(int method, String url, WaspRequest request, VolleyListener<T> listener) {
            super(method, url, listener);
            this.url = url;
            this.listener = listener;
            this.requestBody = request.getBody();
            this.responseObjectType = request.getMethodInfo().getResponseObjectType();
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


                WaspResponse waspResponse = new WaspResponse.Builder()
                        .setUrl(url)
                        .setStatusCode(response.statusCode)
                        .setHeaders(response.headers)
                        .setBody(body)
                        .setLength(data.length)
                        .setNetworkTime(response.networkTimeMs)
                        .build();

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
