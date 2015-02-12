package com.orhanobut.wasp;

import android.graphics.Bitmap;

import com.orhanobut.wasp.utils.WaspCache;

/**
 * @author Orhan Obut
 */
public class BitmapWaspCache extends WaspCache<String, Bitmap> implements WaspImageHandler.ImageCache {

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }

    @Override
    public void clearCache() {
        clearAll();
    }
}
