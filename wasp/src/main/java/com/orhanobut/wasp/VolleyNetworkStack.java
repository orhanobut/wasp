package com.orhanobut.wasp;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.orhanobut.wasp.utils.WaspHttpStack;
import com.orhanobut.wasp.utils.WaspRetryPolicy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class VolleyNetworkStack implements NetworkStack {

  private static final String METHOD_GET = "GET";
  private static final String METHOD_PUT = "PUT";
  private static final String METHOD_POST = "POST";
  private static final String METHOD_DELETE = "DELETE";
  private static final String METHOD_PATCH = "PATCH";
  private static final String METHOD_HEAD = "HEAD";

  private final RequestQueue requestQueue;

  private VolleyNetworkStack(RequestQueue requestQueue) {
    this.requestQueue = requestQueue;
  }

  private VolleyNetworkStack(Context context, WaspHttpStack stack) {
    requestQueue = Volley.newRequestQueue(context, stack.getHttpStack());
  }

  static VolleyNetworkStack newInstance(Context context, WaspHttpStack stack) {
    return new VolleyNetworkStack(context, stack);
  }

  static VolleyNetworkStack newInstance(RequestQueue requestQueue) {
    return new VolleyNetworkStack(requestQueue);
  }

  synchronized RequestQueue getRequestQueue() {
    return requestQueue;
  }

  private Object addToQueueSync(RequestCreator requestCreator) throws Exception {
    final RequestFuture<Object> future = RequestFuture.newFuture();
    Request<Response> request = new VolleyRequest(getMethod(requestCreator.getMethod()),
        requestCreator.getUrl(), requestCreator, future) {
      @Override
      protected void deliverResponse(Response response) {
        super.deliverResponse(response);
        future.onResponse(response.getResponseObject());
      }
    };
    future.setRequest(request);
    addToQueue(request);
    int timeout = 30000;
    if (requestCreator.getRetryPolicy() != null) {
      timeout = requestCreator.getRetryPolicy().getCurrentTimeout();
    }
    return future.get(timeout, TimeUnit.MILLISECONDS);
  }

  private void addToQueue(final RequestCreator waspRequest,
                          InternalCallback<Response> waspCallback) {
    String url = waspRequest.getUrl();
    int method = getMethod(waspRequest.getMethod());
    final VolleyListener listener = new VolleyListener(waspCallback, url);
    Request<Response> request = new VolleyRequest(method, url, waspRequest, listener) {
      @Override
      protected void deliverResponse(Response response) {
        super.deliverResponse(response);
        listener.onResponse(response);
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
      case METHOD_PATCH:
        return Request.Method.PATCH;
      case METHOD_HEAD:
        return Request.Method.HEAD;
      default:
        throw new IllegalArgumentException("Method must be DELETE,POST,PUT,GET,PATCH,HEAD");
    }
  }

  private <T> void addToQueue(Request<T> request) {
    getRequestQueue().add(request);
  }

  @Override
  public void invokeRequest(RequestCreator waspRequest, InternalCallback<Response> waspCallback) {
    addToQueue(waspRequest, waspCallback);
  }

  @Override
  public Object invokeRequest(RequestCreator requestCreator) throws Exception {
    return addToQueueSync(requestCreator);
  }

  private static class VolleyListener implements
      com.android.volley.Response.Listener<Response>,
      com.android.volley.Response.ErrorListener {

    private final InternalCallback waspCallback;
    private final String url;

    VolleyListener(InternalCallback waspCallback, String url) {
      this.waspCallback = waspCallback;
      this.url = url;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onResponse(Response response) {
      waspCallback.onSuccess(response);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
      Response.Builder builder = new Response.Builder().setUrl(url);
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

      waspCallback.onError(new WaspError(builder.build(), errorMessage));
    }
  }

  private static class VolleyRequest extends Request<Response> {

    /**
     * Charset for request.
     */
    private static final String PROTOCOL_CHARSET = "UTF-8";

    private final String requestBody;
    private final String url;
    private final Type responseObjectType;
    private final RequestCreator waspRequest;

    public VolleyRequest(int method, String url, RequestCreator request,
                         final com.android.volley.Response.ErrorListener listener) {
      super(method, url, listener);
      this.url = url;
      this.requestBody = request.getBody();
      this.responseObjectType = request.getMethodInfo().getResponseObjectType();
      this.waspRequest = request;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
      return waspRequest.getHeaders();
    }

    @Override
    protected void deliverResponse(Response response) {
      response.log();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected com.android.volley.Response parseNetworkResponse(NetworkResponse response) {
      try {
        byte[] data = response.data;
        String body = new String(data, HttpHeaderParser.parseCharset(response.headers));
        Object responseObject = Wasp.getParser().fromBody(body, responseObjectType);

        Response waspResponse = new Response.Builder()
            .setUrl(url)
            .setStatusCode(response.statusCode)
            .setHeaders(response.headers)
            .setBody(body)
            .setResponseObject(responseObject)
            .setLength(data.length)
            .setNetworkTime(response.networkTimeMs)
            .build();

        return com.android.volley.Response.success(waspResponse,
            HttpHeaderParser.parseCacheHeaders(response));
      } catch (UnsupportedEncodingException e) {
        return com.android.volley.Response.error(new ParseError(e));
      } catch (IOException e) {
        return com.android.volley.Response.error(new ParseError(e));
      }
    }

    @Override
    public String getBodyContentType() {
      return String.format(
          "%1$s; charset=%2$s",
          waspRequest.getContentType(),
          PROTOCOL_CHARSET
      );
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
      return waspRequest.getFieldParams();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
      byte[] body;
      try {
        body = requestBody == null ? null : requestBody.getBytes(PROTOCOL_CHARSET);
      } catch (UnsupportedEncodingException uee) {
        Logger.wtf("Unsupported Encoding while trying to get the bytes of %s using %s"
                + requestBody
                + PROTOCOL_CHARSET
        );
        return null;
      }
      if (body == null) {
        return super.getBody();
      }
      return body;
    }
  }

}
