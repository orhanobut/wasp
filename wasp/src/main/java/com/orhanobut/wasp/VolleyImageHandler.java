package com.orhanobut.wasp;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

/**
 * @author Orhan Obut
 */
class VolleyImageHandler implements ImageHandler {

    private static ImageLoader imageLoader;
    private static BitmapWaspCache bitmapWaspCache;

    private WaspImage waspImage;
    private ImageLoader.ImageContainer imageContainer;

    private VolleyImageHandler(WaspImage waspImage) {
        this.waspImage = waspImage;
    }

    static void init(Context context) {
        synchronized (VolleyImageHandler.class) {
            bitmapWaspCache = new BitmapWaspCache();
            // imageLoader = new ImageLoader(Volley.newRequestQueue(context), bitmapWaspCache);
        }
    }

    static VolleyImageHandler newHandler(WaspImage waspImage) {
        return new VolleyImageHandler(waspImage);
    }

    public void load() {
        final ImageView imageView = waspImage.getImageView();
        //        final ViewTreeObserver observer = imageView.getViewTreeObserver();
        //        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        //            @Override
        //            public void onGlobalLayout() {
        //                loadImage();
        //                imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        //            }
        //        });

        //
        //        imageView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
        //            @Override
        //            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        //                loadImage(true);
        //            }
        //        });
        //
        //        imageView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
        //            @Override
        //            public void onViewAttachedToWindow(View v) {
        //
        //            }
        //
        //            @Override
        //            public void onViewDetachedFromWindow(View v) {
        //                if (imageContainer != null) {
        //                    // If the view was bound to an image request, cancel it and clear
        //                    // out the image from the view.
        //                    imageContainer.cancelRequest();
        //                    imageView.setImageBitmap(null);
        //                    // also clear out the container so we can reload the image if necessary.
        //                    imageContainer = null;
        //                }
        //            }
        //        });


        loadImage(false);
    }

    @Override
    public void load(WaspImage waspImage) {

    }

    @Override
    public void clearCache() {
        bitmapWaspCache.clearAll();
    }

    private void loadImage(boolean isInLayoutChanged) {
        final ImageView imageView = waspImage.getImageView();
        final String url = waspImage.getUrl();
        final int defaultImage = waspImage.getDefaultImage();

        int width = imageView.getWidth();
        int height = imageView.getHeight();

        boolean wrapWidth = false;
        boolean wrapHeight = false;
        if (imageView.getLayoutParams() != null) {
            wrapWidth = imageView.getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT;
            wrapHeight = imageView.getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        // if the view's bounds aren't known yet, and this is not a wrap-content/wrap-content
        // view, hold off on loading the image.
        boolean isFullyWrapContent = wrapWidth && wrapHeight;
        if (width == 0 && height == 0 && !isFullyWrapContent) {
            Logger.d("VolleyImageHandler : width == 0 && height == 0 && !isFullyWrapContent");
            return;
        }

        // if there was an old request in this view, check if it needs to be canceled.
        if (imageContainer != null) {
            String requestUrl = imageContainer.getRequestUrl();
            if (TextUtils.equals(requestUrl, url)) {
                Logger.d("VolleyImageHandler : requestUrl == url");
                return;
            }
            imageContainer.cancelRequest();
            setDefaultImage(defaultImage, imageView);
        }

        // Calculate the max image width / height to use while ignoring WRAP_CONTENT dimens.
        int maxWidth = wrapWidth ? 0 : width;
        int maxHeight = wrapHeight ? 0 : height;

        imageContainer = imageLoader.get(
                url, new WaspImageListener(imageView, waspImage, isInLayoutChanged), maxWidth, maxHeight
        );
        waspImage.logRequest();
    }

    private void setDefaultImage(int defaultImage, ImageView imageView) {
        if (defaultImage != 0) {
            imageView.setImageResource(defaultImage);
            return;
        }
        imageView.setImageBitmap(null);
    }

    private static class WaspImageListener implements ImageLoader.ImageListener {

        final ImageView imageView;
        final WaspImage waspImage;
        final boolean isInLayoutChanged;

        private WaspImageListener(ImageView imageView, WaspImage waspImage, boolean isInLayoutChanged) {
            this.imageView = imageView;
            this.waspImage = waspImage;
            this.isInLayoutChanged = isInLayoutChanged;
        }

        @Override
        public void onResponse(final ImageLoader.ImageContainer response, boolean isImmediate) {
            // If this was an immediate response that was delivered inside of a layout
            // pass do not set the image immediately as it will trigger a requestLayout
            // inside of a layout. Instead, defer setting the image by posting back to
            // the main thread.
            if (isImmediate && isInLayoutChanged) {
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        onResponse(response, false);
                    }
                });
                return;
            }

            Bitmap bitmap = response.getBitmap();
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                waspImage.logSuccess(bitmap);
                return;
            }

            int defaultImage = waspImage.getDefaultImage();
            if (defaultImage != 0) {
                imageView.setImageResource(defaultImage);
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            String message = error != null ? error.getMessage() : "Null error object receiver";
            long delay = error != null ? error.getNetworkTimeMs() : 0;
            waspImage.logError(message, delay);

            int errorImage = waspImage.getErrorImage();
            if (errorImage != 0) {
                imageView.setImageResource(errorImage);
            }
        }
    }

}
