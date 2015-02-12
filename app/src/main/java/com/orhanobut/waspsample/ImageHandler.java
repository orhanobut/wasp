package com.orhanobut.waspsample;

import android.content.Context;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.orhanobut.wasp.Wasp;

/**
 * @author Orhan Obut
 */
public class ImageHandler {

    static RequestQueue requestQueue;
    static ImageLoader imageLoader;


    public static void volley(Context context, ImageView imageView, String url) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
            //  imageLoader = new ImageLoader(requestQueue, new BitmapWaspCache());
        }

        ((NetworkImageView) imageView).setImageUrl(url, imageLoader);

    }

    public static void wasp(Context context, ImageView imageView, String url) {
        Wasp.Image.from(url).to(imageView).load();
    }
}
