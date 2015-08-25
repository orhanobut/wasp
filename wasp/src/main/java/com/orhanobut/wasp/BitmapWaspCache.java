package com.orhanobut.wasp;

import android.graphics.Bitmap;

import com.orhanobut.wasp.utils.WaspCache;

public class BitmapWaspCache extends WaspCache<String, Bitmap> implements
    InternalImageHandler.ImageCache {

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
