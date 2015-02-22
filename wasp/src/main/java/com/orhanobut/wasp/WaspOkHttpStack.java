package com.orhanobut.wasp;

import com.orhanobut.wasp.utils.WaspHttpStack;
import com.squareup.okhttp.OkHttpClient;

import java.net.CookieHandler;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 * @author Emmar Kardeslik
 */
public class WaspOkHttpStack implements WaspHttpStack<OkHttpStack> {

    private OkHttpStack okHttpStack;

    public WaspOkHttpStack() {
        this(new OkHttpClient());
    }

    public WaspOkHttpStack(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            throw new NullPointerException("OkHttpClient may not be null.");
        }
        okHttpStack = new OkHttpStack(okHttpClient);
    }

    @Override
    public OkHttpStack getHttpStack() {
        return okHttpStack;
    }

    @Override
    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        okHttpStack.getClient().setHostnameVerifier(hostnameVerifier);
    }

    @Override
    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        okHttpStack.getClient().setSslSocketFactory(sslSocketFactory);
    }

    @Override
    public void setCookieHandler(CookieHandler cookieHandler) {
        okHttpStack.getClient().setCookieHandler(cookieHandler);
    }
}
