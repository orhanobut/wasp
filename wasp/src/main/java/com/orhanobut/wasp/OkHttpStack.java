package com.orhanobut.wasp;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.HttpStack;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * An HttpStack implementation which uses {@link com.squareup.okhttp.OkHttpClient} as http client
 */
class OkHttpStack implements HttpStack {

  private final OkHttpClient client;

  OkHttpStack(OkHttpClient client) {
    this.client = client;
  }

  OkHttpClient getClient() {
    return client;
  }

  @Override
  public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders)
      throws IOException, AuthFailureError {

    OkHttpClient okHttpClient = client.clone();

    int timeoutMs = request.getTimeoutMs();
    okHttpClient.setConnectTimeout(timeoutMs, TimeUnit.MILLISECONDS);
    okHttpClient.setReadTimeout(timeoutMs, TimeUnit.MILLISECONDS);
    okHttpClient.setWriteTimeout(timeoutMs, TimeUnit.MILLISECONDS);

    com.squareup.okhttp.Request.Builder builder = new com.squareup.okhttp.Request.Builder();
    builder.url(request.getUrl());

    Map<String, String> headers = request.getHeaders();
    for (String name : headers.keySet()) {
      builder.addHeader(name, headers.get(name));
    }
    for (String name : additionalHeaders.keySet()) {
      builder.addHeader(name, additionalHeaders.get(name));
    }

    setConnectionParametersForRequest(builder, request);

    Call okHttpCall = okHttpClient.newCall(builder.build());
    Response okHttpResponse = okHttpCall.execute();

    StatusLine responseStatus = new BasicStatusLine(
        parseProtocol(okHttpResponse.protocol()), okHttpResponse.code(), okHttpResponse.message()
    );
    BasicHttpResponse response = new BasicHttpResponse(responseStatus);
    response.setEntity(entityFromOkHttpResponse(okHttpResponse));

    Headers responseHeaders = okHttpResponse.headers();
    for (int i = 0, len = responseHeaders.size(); i < len; i++) {
      String name = responseHeaders.name(i), value = responseHeaders.value(i);
      if (name != null) {
        response.addHeader(new BasicHeader(name, value));
      }
    }

    return response;
  }

  private static HttpEntity entityFromOkHttpResponse(Response response) throws IOException {
    BasicHttpEntity entity = new BasicHttpEntity();
    ResponseBody body = response.body();

    entity.setContent(body.byteStream());
    entity.setContentLength(body.contentLength());
    entity.setContentEncoding(response.header("Content-Encoding"));

    if (body.contentType() != null) {
      entity.setContentType(body.contentType().type());
    }
    return entity;
  }

  private static void setConnectionParametersForRequest(
      com.squareup.okhttp.Request.Builder builder, Request<?> request)
      throws IOException, AuthFailureError {
    switch (request.getMethod()) {
      case Request.Method.DEPRECATED_GET_OR_POST:
        // Ensure backwards compatibility.  Volley assumes a request with a null body is a GET.
        byte[] postBody = request.getPostBody();
        if (postBody != null) {
          builder.post(RequestBody.create(
              MediaType.parse(request.getPostBodyContentType()), postBody
          ));
        }
        break;
      case Request.Method.GET:
        builder.get();
        break;
      case Request.Method.DELETE:
        builder.delete();
        break;
      case Request.Method.POST:
        builder.post(createRequestBody(request));
        break;
      case Request.Method.PUT:
        builder.put(createRequestBody(request));
        break;
      case Request.Method.HEAD:
        builder.head();
        break;
      case Request.Method.OPTIONS:
        builder.method("OPTIONS", null);
        break;
      case Request.Method.TRACE:
        builder.method("TRACE", null);
        break;
      case Request.Method.PATCH:
        builder.patch(createRequestBody(request));
        break;
      default:
        throw new IllegalStateException("Unknown method type.");
    }
  }

  private static ProtocolVersion parseProtocol(final Protocol protocol) {
    switch (protocol) {
      case HTTP_1_0:
        return new ProtocolVersion("HTTP", 1, 0);
      case HTTP_1_1:
        return new ProtocolVersion("HTTP", 1, 1);
      case SPDY_3:
        return new ProtocolVersion("SPDY", 3, 1);
      case HTTP_2:
        return new ProtocolVersion("HTTP", 2, 0);
      default:
        throw new IllegalAccessError("Unkwown protocol");
    }
  }

  private static RequestBody createRequestBody(Request request) throws AuthFailureError {
    byte[] body = request.getBody();
    if (body == null) {
      return null;
    }
    return RequestBody.create(MediaType.parse(request.getBodyContentType()), body);
  }
}
