package com.orhanobut.wasp;

import com.android.volley.toolbox.HurlStack;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Emmar Kardeslik
 */
public class OkHttpStack extends HurlStack {

    private final OkUrlFactory factory;

    public OkHttpStack() {
        this(new OkHttpClient());
    }

    public OkHttpStack(final OkHttpClient client) {
        if (client == null) {
            throw new NullPointerException("Client may not be null.");
        }

        this.factory = new OkUrlFactory(client);
    }

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        return factory.open(url);
    }

}
