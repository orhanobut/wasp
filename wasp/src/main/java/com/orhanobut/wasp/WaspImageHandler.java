package com.orhanobut.wasp;

import android.graphics.Bitmap;
import android.os.Looper;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.orhanobut.wasp.utils.StringUtils;

/**
 * This class is responsible of the loading image. It automatically handles the canceling and
 * loading images for the recycled view as well.
 *
 * @author Orhan Obut
 */
final class WaspImageHandler implements ImageHandler {

    /**
     * It is used to determine which url is current for the ImageView
     */
    private static final int KEY_TAG = 0x7f070006;

    /**
     * Stores the cached images
     */
    private final ImageCache imageCache;

    /**
     * It is used to create network request for the bitmap
     */
    private final ImageNetworkHandler imageNetworkHandler;

    WaspImageHandler(ImageCache cache, ImageNetworkHandler handler) {
        this.imageCache = cache;
        this.imageNetworkHandler = handler;
    }

    @Override
    public void load(final WaspImage waspImage) {
        checkMain();
        loadImage(waspImage);
    }

    private void loadImage(final WaspImage waspImage) {
        final String url = waspImage.getUrl();
        final ImageView imageView = waspImage.getImageView();

        // clear the target
        initImageView(waspImage);

        // if there is any old request. cancel it
        String tag = (String) imageView.getTag(KEY_TAG);
        if (tag != null) {
            imageNetworkHandler.cancelRequest(tag);
        }

        // update the current url
        imageView.setTag(KEY_TAG, url);

        int width = imageView.getWidth();
        int height = imageView.getHeight();

        boolean wrapWidth = false;
        boolean wrapHeight = false;
        if (imageView.getLayoutParams() != null) {
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            wrapWidth = params.width == ViewGroup.LayoutParams.WRAP_CONTENT;
            wrapHeight = params.height == ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        // if the view's bounds aren't known yet, and this is not a wrap-content/wrap-content
        // view, hold off on loading the image.
        boolean isFullyWrapContent = wrapWidth && wrapHeight;
        if (width == 0 && height == 0 && !isFullyWrapContent) {
            Logger.d("ImageHandler : width == 0 && height == 0 && !isFullyWrapContent");
            // return;
        }

        // Calculate the max image width / height to use while ignoring WRAP_CONTENT dimens.
        int maxWidth = wrapWidth ? 0 : width;
        int maxHeight = wrapHeight ? 0 : height;

        // check if it is already in cache
        final String cacheKey = StringUtils.getCacheKey(url, maxWidth, maxHeight);
        final Bitmap bitmap = imageCache.getBitmap(cacheKey);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            Logger.d("CACHE IMAGE : " + url);
            return;
        }

        // make a new request
        imageNetworkHandler.requestImage(waspImage, maxWidth, maxHeight, new CallBack<Container>() {
            @Override
            public void onSuccess(final Container container) {
                Bitmap bitmap = container.bitmap;
                if (bitmap == null) {
                    return;
                }

                container.waspImage.logSuccess(bitmap);

                // cache the image
                imageCache.putBitmap(container.cacheKey, container.bitmap);

                ImageView imageView = container.waspImage.getImageView();

                // if it is the current url, set the image
                String tag = (String) imageView.getTag(KEY_TAG);
                if (TextUtils.equals(tag, container.waspImage.getUrl())) {
                    imageView.setImageBitmap(container.bitmap);
                    imageView.setTag(KEY_TAG, null);
                }
            }

            @Override
            public void onError(WaspError error) {
                error.log();
            }
        });

        waspImage.logRequest();
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
            throw new IllegalStateException("Wasp.Image.load() must be invoked from the main thread.");
        }
    }

    /**
     * Simple cache adapter interface.
     */
    interface ImageCache {

        public Bitmap getBitmap(String url);

        public void putBitmap(String url, Bitmap bitmap);

        public void clearCache();
    }

    interface ImageNetworkHandler {

        void requestImage(WaspImage waspImage, int maxWidth, int maxHeight, CallBack<Container> callBack);

        void cancelRequest(String tag);

    }

    static class Container {
        String cacheKey;
        Bitmap bitmap;
        WaspImage waspImage;
    }

}
