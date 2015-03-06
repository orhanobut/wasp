package com.orhanobut.wasp;

import android.graphics.Bitmap;

import static org.assertj.core.api.Assertions.assertThat;
/**
 * Created by yekmer
 */
public class BitmapWaspCacheTest extends BaseTest {

    public void testGetBitmap() {
        BitmapWaspCache bitmapWaspCache = new BitmapWaspCache();
        bitmapWaspCache.put("key", getSampleBitmap());
        assertThat(bitmapWaspCache.get("key")).isNotNull();
    }

    public void testClearCache() {
        BitmapWaspCache bitmapWaspCache = new BitmapWaspCache();
        bitmapWaspCache.put("key", getSampleBitmap());
        bitmapWaspCache.clearAll();
        assertThat(bitmapWaspCache.get("key")).isNull();
    }

    private Bitmap getSampleBitmap() {
        int width = 1;
        int height = 1;
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        return Bitmap.createBitmap(width, height, conf);
    }
}
