package com.orhanobut.wasp;

import android.text.TextUtils;
import android.widget.ImageView;

/**
 * @author Orhan Obut
 */
final class WaspImage {

    private final String url;
    private final ImageView imageView;
    private final ImageHandler imageHandler;
    private final int defaultImage;
    private final int errorImage;
    private final boolean cropCenter;
    private final boolean fit;
    private final Size size;

    /**
     * For now, we will use Volley ImageLoader for the image handling
     */
    private WaspImage(Builder builder) {
        this.imageHandler = new VolleyImageHandler();
        this.url = builder.url;
        this.imageView = builder.imageView;
        this.defaultImage = builder.defaultImage;
        this.errorImage = builder.errorImage;
        this.cropCenter = builder.cropCenter;
        this.fit = builder.fit;
        this.size = builder.size;
    }

    String getUrl() {
        return url;
    }

    ImageView getImageView() {
        return imageView;
    }

    int getDefaultImage() {
        return defaultImage;
    }

    int getErrorImage() {
        return errorImage;
    }

    boolean isCropCenter() {
        return cropCenter;
    }

    boolean isFit() {
        return fit;
    }

    Size getSize() {
        return size;
    }

    void load() {
        imageHandler.init(this);
        imageHandler.load();
    }

    public static class Builder {

        private String url;
        private ImageView imageView;
        private int defaultImage;
        private int errorImage;
        private boolean cropCenter;
        private boolean fit;
        private Size size;

        /**
         * It is used to fetch the image from network
         *
         * @param url is the full url
         * @return Builder
         */
        Builder from(String url) {
            this.url = url;
            return this;
        }

        /**
         * Fetched image will be loaded into this image view.
         *
         * @param imageView is the container
         * @return Builder
         */
        public Builder to(ImageView imageView) {
            if (imageView == null) {
                throw new NullPointerException("ImageView cannot be null");
            }
            this.imageView = imageView;
            return this;
        }

        /**
         * It will be used as default image
         *
         * @param resId is the drawable id
         * @return Builder
         */
        public Builder setDefault(int resId) {
            this.defaultImage = resId;
            return this;
        }

        /**
         * This will be used as image if there is any error
         *
         * @param resId is the drawable id
         * @return Builder itself
         */
        public Builder setError(int resId) {
            this.errorImage = resId;
            return this;
        }

        //TODO
        public Builder cropCenter() {
            this.cropCenter = true;
            return this;
        }

        //TODO
        public Builder fit() {
            this.fit = true;
            return this;
        }

        //TODO 
        public Builder resize(int width, int height) {
            this.size = new Size(width, height);
            return this;
        }

        /**
         * This should be called to fetch the image
         */
        public void load() {
            WaspImage waspImage = new WaspImage(this);
            waspImage.load();
        }
    }

    /**
     * Immutable size data holder
     */
    static class Size {
        private final int width;
        private final int height;

        Size(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }
}
