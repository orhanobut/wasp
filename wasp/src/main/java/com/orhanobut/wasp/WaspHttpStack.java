package com.orhanobut.wasp;

import javax.net.ssl.SSLSocketFactory;

/**
 * @author Emmar Kardeslik
 */
public interface WaspHttpStack<T> {

    public T getHttpStack();

    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory);

}
