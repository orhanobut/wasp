package com.orhanobut.wasp;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.orhanobut.wasp.utils.StringUtils;
import com.orhanobut.wasp.utils.WaspHttpStack;

import java.io.UnsupportedEncodingException;

/**
 * @author Orhan Obut
 */
public class VolleyImageNetworkHandler implements WaspImageHandler.ImageNetworkHandler {

    private final RequestQueue requestQueue;

    public VolleyImageNetworkHandler(Context context, WaspHttpStack httpStack) {
        this.requestQueue = Volley.newRequestQueue(context, (HttpStack) httpStack);
    }

    @Override
    public void requestImage(final WaspImage waspImage, final int maxWidth, final int maxHeight,
                             final CallBack<WaspImageHandler.Container> callBack) {

        final String url = waspImage.getUrl();
        Logger.d("REQUEST IMAGE -> url : " + url);
        Request<Bitmap> request = new ImageRequest(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        Logger.i("SUCCESS -> url : " + url);
                        WaspImageHandler.Container container = new WaspImageHandler.Container();
                        container.bitmap = response;
                        container.cacheKey = StringUtils.getCacheKey(url, maxWidth, maxHeight);
                        container.waspImage = waspImage;
                        callBack.onSuccess(container);
                    }
                },
                maxWidth,
                maxHeight,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
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

                        callBack.onError(new WaspError(builder.build(), errorMessage));
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
