package com.orhanobut.wasp.utils;

import com.android.volley.toolbox.HttpStack;

import java.net.CookieHandler;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 * @author Emmar Kardeslik
 */
public interface WaspHttpStack<T extends HttpStack> {

    /**
     * Get HttpStack that is used in {@link com.orhanobut.wasp.VolleyNetworkStack}
     *
     * @return instance of HttpStack
     */
    public T getHttpStack();

    /**
     * Set hostnameVerifier of the http client used by HttpStack
     * This is required since Wasp sets an empty hostnameVerifier for trust all certificates feature
     *
     * @param hostnameVerifier
     */
    public void setHostnameVerifier(HostnameVerifier hostnameVerifier);

    /**
     * Set sslSocketFactory of the http client used by HttpStack
     * This is required since Wasp sets an sslSocketFactory either for trust all certificates
     * or for a specific certificate provided via a keystore
     *
     * @param sslSocketFactory
     */
    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory);

    /**
     * Set cookieHandler of the http client used by HttpStack
     * This is required since Wasp provides cookie handler feature
     *
     * @param cookieHandler
     */
    public void setCookieHandler(CookieHandler cookieHandler);

}
