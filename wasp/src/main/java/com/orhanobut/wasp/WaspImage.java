package com.orhanobut.wasp;

import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;

/**
 * @author Orhan Obut
 */
public final class WaspImage {

    private static WaspImage waspImage;
    private static ImageLoader imageLoader;
    private static WaspBitmapCache bitmapCache;

    private String url;
    private ImageView imageView;

    private WaspImage() {
        // no instance
    }

    private static WaspImage getInstance() {
        if (waspImage == null) {
            synchronized (WaspImage.class) {
                waspImage = new WaspImage();
                bitmapCache = new WaspBitmapCache();
            }
        }
        return waspImage;
    }

    private static ImageLoader getImageLoader() {
        if (imageLoader == null) {
            synchronized (imageLoader) {
                imageLoader = new ImageLoader(VolleyNetworkStack.getRequestQueue(), bitmapCache);
            }
        }
        return imageLoader;
    }

    public static WaspImage from(String path) {
        WaspImage wasp = getInstance();
        wasp.url = path;

        return wasp;
    }

    public  WaspImage to(ImageView imageView) {
        WaspImage wasp = getInstance();
        wasp.imageView = imageView;
        return wasp;
    }

    public void load() {
        WaspImage wasp = getInstance();
        imageLoader.get(wasp.url, ImageLoader.getImageListener(
                wasp.imageView,
                0,
                0
        ));
    }

}
