package com.orhanobut.wasp.utils;

import android.graphics.Bitmap;

import com.android.volley.toolbox.ImageLoader;

/**
 * @author Orhan Obut
 */
public class WaspBitmapCache extends WaspCache<String, Bitmap> implements ImageLoader.ImageCache {

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }
}
