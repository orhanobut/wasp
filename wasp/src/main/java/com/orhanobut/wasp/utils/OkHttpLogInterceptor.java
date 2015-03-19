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
 * An {@link com.squareup.okhttp.Interceptor} implementation which logs all info about outgoing request and related response
 * including complete header set, request and response bodies and network time
 *
 * @author Emmar Kardeslik
 */
public class OkHttpLogInterceptor implements Interceptor {

    private static final double MILLI_AS_NANO = 1e6d;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //keep original request's body in order not to lose it when logging body below
        Request.Builder originalRequestBuilder = request.newBuilder()
                .method(request.method(), request.body());
        Logger.d("---> REQUEST " + request.method() + " " + request.urlString());
        logHeaders(request.headers());
        RequestBody requestBody = request.body();
        if (requestBody == null) {
            Logger.d("Body - no body");
        } else {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Logger.d("Body - " + buffer.readString(requestBody.contentType().charset()));
        }
        Logger.d("---> END");

        long t1 = System.nanoTime();
        Response response = chain.proceed(originalRequestBuilder.build());
        long t2 = System.nanoTime();

        Logger.d("<--- RESPONSE " + response.code() + " " + response.request().urlString());
        logHeaders(response.headers());
        String responseBody = response.body().string();
        Logger.d(TextUtils.isEmpty(responseBody) ? "Body - no body" : "Body - " + responseBody);
        Logger.d("<--- END " + "(Size: " + response.body().contentLength() + " bytes - "+
                "Network time: " + (t2 - t1) / MILLI_AS_NANO + " ms)");

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
