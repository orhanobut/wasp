package com.orhanobut.wasp;

import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.orhanobut.wasp.toolbox.WaspBitmapCache;

/**
 * @author Orhan Obut
 */
class VolleyImageHandler implements ImageHandler {

    private static WaspBitmapCache bitmapCache;
    private static ImageLoader imageLoader;

    private WaspImage waspImage;

    /**
     * Current ImageContainer. (either in-flight or finished)
     */
    private ImageLoader.ImageContainer imageContainer;

    @Override
    public void init(WaspImage waspImage) {
        this.waspImage = waspImage;
        if (bitmapCache == null) {
            bitmapCache = new WaspBitmapCache();
        }
        if (imageLoader == null) {
            imageLoader = new ImageLoader(VolleyNetworkStack.getRequestQueue(), bitmapCache);
        }
    }

    @Override
    public void load() {
        loadImage();
    }

    public void loadImage() {
        final ImageView imageView = waspImage.getImageView();
        final String url = waspImage.getUrl();
        final int defaultImage = waspImage.getDefaultImage();
        final int errorImage = waspImage.getErrorImage();

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
            return;
        }

        // if the URL to be loaded in this view is empty, cancel any old requests and clear the
        // currently loaded image.
        if (TextUtils.isEmpty(url)) {
            if (imageContainer != null) {
                imageContainer.cancelRequest();
                imageContainer = null;
            }
            setDefaultImage(defaultImage, imageView);
            return;
        }

        // if there was an old request in this view, check if it needs to be canceled.
        if (imageContainer != null && imageContainer.getRequestUrl() != null) {
            if (imageContainer.getRequestUrl().equals(url)) {
                // if the request is from the same URL, return.
                return;
            } else {
                // if there is a pre-existing request, cancel it if it's fetching a different URL.
                imageContainer.cancelRequest();
                setDefaultImage(defaultImage, imageView);
            }
        }

        // Calculate the max image width / height to use while ignoring WRAP_CONTENT dimens.
        int maxWidth = wrapWidth ? 0 : width;
        int maxHeight = wrapHeight ? 0 : height;

        // The pre-existing content of this view didn't match the current URL. Load the new image
        // from the network.
        imageLoader.get(
                url,
                new ImageLoader.ImageListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (errorImage != 0) {
                            imageView.setImageResource(errorImage);
                        }
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

                        if (response.getBitmap() != null) {
                            imageView.setImageBitmap(response.getBitmap());
                        } else if (waspImage.getDefaultImage() != 0) {
                            imageView.setImageResource(defaultImage);
                        }
                    }
                },
                maxWidth,
                maxHeight
        );
    }

    private void setDefaultImage(int placeHolder, ImageView imageView) {
        if (placeHolder != 0) {
            imageView.setImageResource(placeHolder);
            return;
        }
        imageView.setImageBitmap(null);
    }

}
