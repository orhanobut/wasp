package com.orhanobut.wasp;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.orhanobut.wasp.utils.StringUtils;
import com.orhanobut.wasp.utils.WaspHttpStack;

import java.io.UnsupportedEncodingException;

public class VolleyImageNetworkHandler implements InternalImageHandler.ImageNetworkHandler {

  private final RequestQueue requestQueue;

  public VolleyImageNetworkHandler(Context context, WaspHttpStack stack) {
    //    requestQueue = Volley.newRequestQueue(context);
    requestQueue = Volley.newRequestQueue(context, stack.getHttpStack());
  }

  @Override
  public void requestImage(final ImageCreator waspImageCreator, final int maxWidth,
                           final int maxHeight,
                           final InternalCallback<InternalImageHandler.Container> waspCallback) {

    final String url = waspImageCreator.getUrl();
    Logger.d("REQUEST IMAGE -> url : " + url);
    Request<Bitmap> request = new ImageRequest(
        url,
        new com.android.volley.Response.Listener<Bitmap>() {
          @Override
          public void onResponse(Bitmap response) {
            Logger.i("SUCCESS -> url : " + url);
            InternalImageHandler.Container container = new InternalImageHandler.Container();
            container.bitmap = response;
            container.cacheKey = StringUtils.getCacheKey(url, maxWidth, maxHeight);
            container.waspImageCreator = waspImageCreator;
            waspCallback.onSuccess(container);
          }
        },
        maxWidth,
        maxHeight,
        Bitmap.Config.RGB_565,
        new com.android.volley.Response.ErrorListener() {
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
                      error.networkResponse.data,
                      HttpHeaderParser.parseCharset(response.headers)
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
    );
    request.setTag(url);
    requestQueue.add(request);
  }

  @Override
  public void cancelRequest(final String tag) {
    Logger.w("CANCEL REQUEST -> url : " + tag);
    RequestQueue.RequestFilter filter = new RequestQueue.RequestFilter() {
      @Override
      public boolean apply(Request<?> request) {
        return tag.equals(request.getTag());
      }
    };
    requestQueue.cancelAll(filter);
  }

}
