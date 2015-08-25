package com.orhanobut.wasp;

import com.orhanobut.wasp.utils.WaspHttpStack;
import com.squareup.okhttp.OkHttpClient;

import java.net.CookieHandler;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 * Wasp's default {@link com.orhanobut.wasp.utils.WaspHttpStack} implementation which uses
 * {@link com.squareup.okhttp.OkHttpClient} as http client
 */
public class WaspOkHttpStack implements WaspHttpStack<OkHttpStack> {

  private final OkHttpStack okHttpStack;

  public WaspOkHttpStack() {
    this(new OkHttpClient());
  }

  public WaspOkHttpStack(OkHttpClient okHttpClient) {
    if (okHttpClient == null) {
      throw new NullPointerException("OkHttpClient may not be null.");
    }
    okHttpStack = new OkHttpStack(okHttpClient);
  }

  /**
   * {@inheritDoc}
   *
   * @return {@link com.orhanobut.wasp.OkHttpStack}
   */
  @Override
  public OkHttpStack getHttpStack() {
    return okHttpStack;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
    okHttpStack.getClient().setHostnameVerifier(hostnameVerifier);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
    okHttpStack.getClient().setSslSocketFactory(sslSocketFactory);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCookieHandler(CookieHandler cookieHandler) {
    okHttpStack.getClient().setCookieHandler(cookieHandler);
  }

}