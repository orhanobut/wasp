package com.orhanobut.wasp;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.orhanobut.wasp.utils.StringUtils;

/**
 * @author Orhan Obut
 */
public class VolleyImageNetworkHandler implements WaspImageHandler.ImageNetworkHandler {

    private final RequestQueue requestQueue;

    public VolleyImageNetworkHandler(Context context) {
        this.requestQueue = Volley.newRequestQueue(context);
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
                        container.imageView = waspImage.getImageView();
                        container.url = url;
                        callBack.onSuccess(container);
                    }
                },
                maxWidth,
                maxHeight,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callBack.onError(new WaspError(null, error.getMessage()));
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
