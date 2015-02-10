package com.orhanobut.wasp;

import android.graphics.Bitmap;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

import com.orhanobut.wasp.utils.StringUtils;


/**
 * @author Orhan Obut
 */
final class WaspImageHandler implements ImageHandler {

    private static final int KEY_TAG = 0x7f070006;

    private final ImageCache imageCache;
    private final ImageNetworkHandler imageNetworkHandler;

    WaspImageHandler(ImageCache cache, ImageNetworkHandler handler) {
        this.imageCache = cache;
        this.imageNetworkHandler = handler;
    }

    @Override
    public void load(WaspImage waspImage) {
        checkMain();

        final String url = waspImage.getUrl();
        final ImageView imageView = waspImage.getImageView();
        final String cacheKey = StringUtils.getCacheKey(url, 0, 0);

        // clear the target
        initImageView(waspImage);

        // if there is any old request. cancel it
        String tag = (String) imageView.getTag(KEY_TAG);
        if (tag != null) {
            imageNetworkHandler.cancelRequest(tag);
        }

        // check if it is already in cache
        final Bitmap bitmap = imageCache.getBitmap(cacheKey);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            Logger.d("CACHE IMAGE : " + url);
            return;
        }

        // update the current url
        imageView.setTag(KEY_TAG, url);

        // make a new request
        imageNetworkHandler.requestImage(waspImage, 0, 0, new CallBack<Container>() {
            @Override
            public void onSuccess(final Container container) {
                ImageView imageView1 = container.imageView;
                Bitmap bitmap1 = container.bitmap;
                if (bitmap1 == null) {
                    return;
                }

                // cache the image
                imageCache.putBitmap(container.cacheKey, container.bitmap);

                // if it is the current url, set the image
                String tag = (String) imageView1.getTag(KEY_TAG);
                if (TextUtils.equals(tag, container.url)) {
                    imageView1.setImageBitmap(container.bitmap);
                    imageView1.setTag(KEY_TAG, null);
                }
            }

            @Override
            public void onError(WaspError error) {

            }
        });
    }

    // clear the target by setting null or default placeholder
    private void initImageView(WaspImage waspImage) {
        int defaultImage = waspImage.getDefaultImage();
        ImageView imageView = waspImage.getImageView();
        if (defaultImage != 0) {
            imageView.setImageResource(defaultImage);
            return;
        }
        imageView.setImageBitmap(null);
    }

    @Override
    public void clearCache() {
        if (imageCache == null) {
            return;
        }
        imageCache.clearCache();
    }

    // the call should be done in main thread
    private void checkMain() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("ImageLoader must be invoked from the main thread.");
        }
    }

    /**
     * Simple cache adapter interface. If provided to the ImageLoader, it
     * will be used as an L1 cache before dispatch to Volley. Implementations
     * must not block. Implementation with an LruCache is recommended.
     */
    public interface ImageCache {

        public Bitmap getBitmap(String url);

        public void putBitmap(String url, Bitmap bitmap);

        public void clearCache();
    }

    public interface ImageNetworkHandler {

        void requestImage(WaspImage waspImage, int maxWidth, int maxHeight, CallBack<Container> callBack);

        void cancelRequest(String tag);

    }

    public static class Container {
        String cacheKey;
        Bitmap bitmap;
        ImageView imageView;
        String url;
    }

}
