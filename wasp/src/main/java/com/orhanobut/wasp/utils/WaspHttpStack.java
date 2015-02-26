package com.orhanobut.wasp.utils;

import com.android.volley.toolbox.HttpStack;

import java.net.CookieHandler;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 * @author Emmar Kardeslik
 */
public interface WaspHttpStack<T extends HttpStack> {

    public T getHttpStack();

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier);

    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory);

    public void setCookieHandler(CookieHandler cookieHandler);

}
