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
import com.orhanobut.wasp.utils.WaspHttpStack;
import com.orhanobut.wasp.utils.WaspRetryPolicy;

import java.io.IOException;
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

    private VolleyNetworkStack(Context context, WaspHttpStack stack) {
        requestQueue = Volley.newRequestQueue(context, stack.getHttpStack());
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

    private <T> void addToQueue(final WaspRequest waspRequest, CallBack<T> callBack) {
        String url = waspRequest.getUrl();
        int method = getMethod(waspRequest.getMethod());
        VolleyListener<T> listener = new VolleyListener<>(callBack, url);
        Request<T> request = new VolleyRequest<T>(method, url, waspRequest, listener) {
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

        VolleyListener(CallBack callBack, String url) {
            this.callBack = callBack;
            this.url = url;
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

            callBack.onError(new WaspError(builder.build(), errorMessage));
        }
    }

    private static class VolleyRequest<T> extends Request<T> {

        /**
         * Charset for request.
         */
        private static final String PROTOCOL_CHARSET = "UTF-8";

        /**
         * Content type for request.
         */
        private static final String PROTOCOL_CONTENT_TYPE = String.format(
                "%1$s; charset=%2$s",
                Wasp.getParser().getSupportedContentType(),
                PROTOCOL_CHARSET
        );

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
        @SuppressWarnings("unchecked")
        protected Response parseNetworkResponse(NetworkResponse response) {
            try {
                byte[] data = response.data;
                String body = new String(data, HttpHeaderParser.parseCharset(response.headers));
                Object responseObject = Wasp.getParser().fromBody(body, responseObjectType);

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
            } catch (IOException e) {
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
