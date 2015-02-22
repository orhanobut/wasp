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
import com.android.volley.toolbox.Volley;
import com.orhanobut.wasp.parsers.Parser;
import com.orhanobut.wasp.utils.WaspHttpStack;
import com.orhanobut.wasp.utils.WaspRetryPolicy;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
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
    private final Parser parser;

    private VolleyNetworkStack(Context context, WaspHttpStack stack, Parser parser) {
        requestQueue = Volley.newRequestQueue(context, stack.getHttpStack());
        // requestQueue = Volley.newRequestQueue(context);
        this.parser = parser;
    }

    static VolleyNetworkStack newInstance(Context context, WaspHttpStack stack, Parser parser) {
        return new VolleyNetworkStack(context, stack, parser);
    }

    synchronized RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            throw new NullPointerException("Wasp.Builder must be called");
        }
        return requestQueue;
    }

    private <T> void addToQueue(final WaspRequest waspRequest, CallBack<T> callBack) {
        String url = waspRequest.getUrl();
        int method = getMethod(waspRequest.getMethod());
        VolleyListener<T> listener = new VolleyListener<>(callBack, url, parser);
        Request<T> request = new VolleyRequest<T>(method, url, waspRequest, listener, parser) {
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
        private final Parser parser;

        VolleyListener(CallBack callBack, String url, Parser parser) {
            this.callBack = callBack;
            this.url = url;
            this.parser = parser;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onResponse(T response) {
            callBack.onSuccess(response);
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            WaspResponse.Builder builder = new WaspResponse.Builder().setUrl(url);
            String errorMessage = null;

            if (error != null) {
                builder.setNetworkTime(error.getNetworkTimeMs());
                errorMessage = error.getMessage();

                if (error.networkResponse != null) {
                    NetworkResponse response = error.networkResponse;
                    String body;
                    try {
                        body = new String(
                                error.networkResponse.data, HttpHeaderParser.parseCharset(response.headers)
                        );
                    } catch (UnsupportedEncodingException e) {
                        body = "Unable to parse error body!!!!!";
                    }
                    builder.setStatusCode(response.statusCode)
                            .setHeaders(response.headers)
                            .setBody(body)
                            .setLength(response.data.length);
                }
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
        private final Parser parser;

        public VolleyRequest(int method, String url, WaspRequest request, VolleyListener<T> listener, Parser parser) {
            super(method, url, listener);
            this.url = url;
            this.listener = listener;
            this.requestBody = request.getBody();
            this.responseObjectType = request.getMethodInfo().getResponseObjectType();
            this.parser = parser;
        }

        @Override
        protected void deliverResponse(T response) {
            listener.onResponse(response);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Response parseNetworkResponse(NetworkResponse response) {
            try {
                byte[] data = response.data;
                String body = new String(data, HttpHeaderParser.parseCharset(response.headers));
                Object responseObject = null;
                try {
                    responseObject = parser.fromJson(body, responseObjectType);
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }

                WaspResponse waspResponse = new WaspResponse.Builder()
                        .setUrl(url)
                        .setStatusCode(response.statusCode)
                        .setHeaders(response.headers)
                        .setBody(body)
                        .setResponseObject(responseObject)
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
