package com.orhanobut.wasp.utils;

import com.android.volley.toolbox.HttpStack;

import java.net.CookieHandler;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

public interface WaspHttpStack<T extends HttpStack> {

  /**
   * Get HttpStack that is used in {@link com.orhanobut.wasp.VolleyNetworkStack}
   *
   * @return instance of HttpStack
   */
  T getHttpStack();

  /**
   * Set hostnameVerifier of the http client used by HttpStack
   * This is required since Wasp sets an empty hostnameVerifier for trust all certificates feature
   *
   * @param hostnameVerifier
   */
  void setHostnameVerifier(HostnameVerifier hostnameVerifier);

  /**
   * Set sslSocketFactory of the http client used by HttpStack
   * This is required since Wasp sets an sslSocketFactory either for trust all certificates
   * or for a specific certificate provided via a keystore
   *
   * @param sslSocketFactory
   */
  void setSslSocketFactory(SSLSocketFactory sslSocketFactory);

  /**
   * Set cookieHandler of the http client used by HttpStack
   * This is required since Wasp provides cookie handler feature
   *
   * @param cookieHandler
   */
  void setCookieHandler(CookieHandler cookieHandler);

}
