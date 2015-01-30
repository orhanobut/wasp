package com.orhanobut.wasp;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.orhanobut.wasp.utils.WaspBitmapCache;

/**
 * @author Orhan Obut
 */
class VolleyImageHandler implements ImageHandler {

    private ImageLoader imageLoader;
    private WaspImage waspImage;

    /**
     * Current ImageContainer. (either in-flight or finished)
     */
    private ImageLoader.ImageContainer imageContainer;

    VolleyImageHandler(Context context) {
        this.imageLoader = new ImageLoader(
                Volley.newRequestQueue(context),
                new WaspBitmapCache()
        );
    }

    @Override
    public void init(WaspImage waspImage) {
        this.waspImage = waspImage;
    }

    @Override
    public void load() {
        final ImageView imageView = waspImage.getImageView();
        final ViewTreeObserver observer = imageView.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                loadImage();
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
    }

    private void loadImage() {
        final ImageView imageView = waspImage.getImageView();
        final String url = waspImage.getUrl();
        final int defaultImage = waspImage.getDefaultImage();

        int width = imageView.getMeasuredWidth();
        int height = imageView.getMeasuredHeight();

        boolean wrapWidth = false;
        boolean wrapHeight = false;
        if (imageView.getLayoutParams() != null) {
            wrapWidth = imageView.getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT;
            wrapHeight = imageView.getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        // if there was an old request in this view, check if it needs to be canceled.
        if (imageContainer != null) {
            String requestUrl = imageContainer.getRequestUrl();
            if (TextUtils.equals(requestUrl, url)) {
                return;
            }
            imageContainer.cancelRequest();
            setDefaultImage(defaultImage, imageView);
        }

        // Calculate the max image width / height to use while ignoring WRAP_CONTENT dimens.
        int maxWidth = wrapWidth ? 0 : width;
        int maxHeight = wrapHeight ? 0 : height;

        imageContainer = imageLoader.get(url, new WaspImageListener(imageView, waspImage), maxWidth, maxHeight);
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

        private WaspImageListener(ImageView imageView, WaspImage waspImage) {
            this.imageView = imageView;
            this.waspImage = waspImage;
        }

        @Override
        public void onResponse(final ImageLoader.ImageContainer response, boolean isImmediate) {
            // If this was an immediate response that was delivered inside of a layout
            // pass do not set the image immediately as it will trigger a requestLayout
            // inside of a layout. Instead, defer setting the image by posting back to
            // the main thread.
            if (isImmediate) {
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
