package com.orhanobut.wasp;

import com.android.volley.toolbox.HttpStack;

import javax.net.ssl.SSLSocketFactory;

/**
 * @author Emmar Kardeslik
 */
public interface WaspHttpStack {

    public HttpStack getHttpStack();

    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory);

}
