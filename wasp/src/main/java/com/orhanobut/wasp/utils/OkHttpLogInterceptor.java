package com.orhanobut.wasp.utils;

import android.text.TextUtils;

import com.orhanobut.wasp.Logger;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

import okio.Buffer;

/**
 * An {@link com.squareup.okhttp.Interceptor} implementation which logs all info about
 * outgoing request and related response including complete header set, request
 * and response bodies and network time
 */
public class OkHttpLogInterceptor implements Interceptor {

  private static final double MILLI_AS_NANO = 1e6d;

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    Logger.d("---> REQUEST " + request.method() + " " + request.urlString());
    logHeaders(request.headers());
    //copy original request for logging request body
    Request copy = request.newBuilder().build();
    RequestBody requestBody = copy.body();
    if (requestBody == null) {
      Logger.d("Body - no body");
    } else {
      Buffer buffer = new Buffer();
      requestBody.writeTo(buffer);
      Logger.d("Body - " + buffer.readString(requestBody.contentType().charset()));
    }
    Logger.d("---> END");

    long t1 = System.nanoTime();
    Response response = chain.proceed(request);
    long t2 = System.nanoTime();

    Logger.d("<--- RESPONSE " + response.code() + " " + response.request().urlString());
    logHeaders(response.headers());
    String responseBody = response.body().string();
    Logger.d(TextUtils.isEmpty(responseBody) ? "Body - no body" : "Body - " + responseBody);
    Logger.d("<--- END " + "(Size: " + response.body().contentLength() + " bytes - "
        + "Network time: " + (t2 - t1) / MILLI_AS_NANO + " ms)");

    return response;
  }

  private static void logHeaders(Headers headers) {
    for (String headerName : headers.names()) {
      for (String headerValue : headers.values(headerName)) {
        Logger.d("Header - [" + headerName + ": " + headerValue + "]");
      }
    }
  }
}
