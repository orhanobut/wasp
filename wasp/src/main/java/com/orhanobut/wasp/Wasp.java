package com.orhanobut.wasp;

import android.content.Context;
import android.text.TextUtils;

import com.orhanobut.wasp.parsers.GsonParser;
import com.orhanobut.wasp.parsers.Parser;
import com.orhanobut.wasp.utils.LogLevel;
import com.orhanobut.wasp.utils.NetworkMode;
import com.orhanobut.wasp.utils.RequestInterceptor;
import com.orhanobut.wasp.utils.SSLUtils;
import com.orhanobut.wasp.utils.WaspHttpStack;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

public class Wasp {

  private static Context context;
  private static LogLevel logLevel;
  private static Parser parser;
  private static WaspHttpStack httpStack;

  private final Builder builder;

  private Wasp(Builder builder) {
    this.builder = builder;

    logLevel = builder.getLogLevel();
    context = builder.getContext();
    parser = builder.getParser();
    httpStack = builder.getWaspHttpStack();
  }

  /**
   * It is used for the parse operations.
   */
  public static Parser getParser() {
    if (parser == null) {
      throw new NullPointerException("Wasp.Builder must be called first");
    }
    return parser;
  }

  static LogLevel getLogLevel() {
    return logLevel;
  }

  @SuppressWarnings("unchecked")
  public <T> T create(Class<T> service) {
    if (service == null) {
      throw new NullPointerException("service param may not be null");
    }
    if (!service.isInterface()) {
      throw new IllegalArgumentException("Only interface type is supported");
    }
    NetworkHandler handler = NetworkHandler.newInstance(service, builder);
    return (T) handler.getProxyClass();
  }

  /**
   * Initiate download and load image process
   */
  public static class Image {

    private static ImageHandler imageHandler;

    public static ImageCreator.Builder from(String path) {
      if (TextUtils.isEmpty(path)) {
        throw new IllegalArgumentException("Path cannot be empty or null");
      }
      return new ImageCreator.Builder()
          .setImageHandler(getImageHandler())
          .from(path);
    }

    private static ImageHandler getImageHandler() {
      if (context == null) {
        throw new NullPointerException("Wasp.Builder should be instantiated first");
      }
      if (imageHandler == null) {
        imageHandler = new InternalImageHandler(
            new BitmapWaspCache(), new VolleyImageNetworkHandler(context, httpStack)
        );
      }
      return imageHandler;
    }

    public static void clearCache() {
      if (imageHandler == null) {
        return;
      }
      imageHandler.clearCache();
    }

  }

  /**
   * Initiate all required information for the wasp
   */
  @SuppressWarnings("unused")
  public static class Builder {

    private String endPointUrl;
    private LogLevel logLevel;
    private NetworkMode networkMode;
    private Context context;
    private Parser parser;
    private WaspHttpStack waspHttpStack;
    private RequestInterceptor requestInterceptor;
    private NetworkStack networkStack;
    private HostnameVerifier hostnameVerifier;
    private SSLSocketFactory sslSocketFactory;
    private CookieHandler cookieHandler;

    public Builder(Context context) {
      if (context == null) {
        throw new NullPointerException("Context should not be null");
      }
      this.context = context;
    }

    public Builder setEndpoint(String url) {
      if (url == null || url.trim().length() == 0) {
        throw new NullPointerException("End point url may not be null or empty");
      }
      if (url.charAt(url.length() - 1) == '/') {
        throw new IllegalArgumentException("End point should not end with \"/\"");
      }
      this.endPointUrl = url;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder setWaspHttpStack(WaspHttpStack waspHttpStack) {
      if (waspHttpStack == null) {
        throw new NullPointerException("WaspHttpStack may not be null");
      }
      if (waspHttpStack.getHttpStack() == null) {
        throw new NullPointerException("WaspHttpStack.getHttpStack() may not return null");
      }
      this.waspHttpStack = waspHttpStack;
      return this;
    }

    public WaspHttpStack getWaspHttpStack() {
      if (waspHttpStack == null) {
        waspHttpStack = new WaspOkHttpStack();
      }
      waspHttpStack.setHostnameVerifier(hostnameVerifier);
      waspHttpStack.setSslSocketFactory(sslSocketFactory);
      waspHttpStack.setCookieHandler(cookieHandler);
      return this.waspHttpStack;
    }

    @SuppressWarnings("unused")
    public Builder trustCertificates() {
      if (sslSocketFactory != null) {
        throw new IllegalStateException("Only one type of trust certificate method can be used!");
      }
      this.sslSocketFactory = SSLUtils.getTrustAllCertSslSocketFactory();
      this.hostnameVerifier = SSLUtils.getEmptyHostnameVerifier();
      return this;
    }

    @SuppressWarnings("unused")
    public Builder trustCertificates(int keyStoreRawResId, String keyStorePassword) {
      if (sslSocketFactory != null) {
        throw new IllegalStateException("Only one type of trust certificate method can be used!");
      }
      this.sslSocketFactory = SSLUtils.getPinnedCertSslSocketFactory(
          context, keyStoreRawResId, keyStorePassword
      );
      return this;
    }

    @SuppressWarnings("unused")
    public Builder enableCookies(CookiePolicy cookiePolicy) {
      return enableCookies(null, cookiePolicy);
    }

    public Builder enableCookies(CookieStore cookieStore, CookiePolicy cookiePolicy) {
      if (cookiePolicy == null) {
        throw new NullPointerException("CookiePolicy may not be null");
      }
      this.cookieHandler = new CookieManager(cookieStore, cookiePolicy);
      return this;
    }

    String getEndPointUrl() {
      if (endPointUrl == null) {
        throw new NullPointerException("Endpoint may not be null");
      }
      return endPointUrl;
    }

    LogLevel getLogLevel() {
      if (logLevel == null) {
        logLevel = LogLevel.NONE;
      }
      return logLevel;
    }

    public Builder setLogLevel(LogLevel logLevel) {
      if (logLevel == null) {
        throw new NullPointerException("Log level should not be null");
      }
      this.logLevel = logLevel;
      return this;
    }

    NetworkMode getNetworkMode() {
      if (networkMode == null) {
        networkMode = NetworkMode.LIVE;
      }
      return networkMode;
    }

    @SuppressWarnings("unused")
    public Builder setNetworkMode(NetworkMode networkMode) {
      if (networkMode == null) {
        throw new NullPointerException("NetworkMode should not be null");
      }
      this.networkMode = networkMode;
      return this;
    }

    Context getContext() {
      return context;
    }

    Parser getParser() {
      if (parser == null) {
        parser = new GsonParser();
      }
      return parser;
    }

    public Builder setParser(Parser parser) {
      if (parser == null) {
        throw new NullPointerException("Parser may not be null");
      }
      this.parser = parser;
      return this;
    }

    RequestInterceptor getRequestInterceptor() {
      return requestInterceptor;
    }

    @SuppressWarnings("unused")
    public Builder setRequestInterceptor(RequestInterceptor interceptor) {
      this.requestInterceptor = interceptor;
      return this;
    }

    public Builder setNetworkStack(NetworkStack networkStack) {
      this.networkStack = networkStack;
      return this;
    }

    public NetworkStack getNetworkStack() {
      if (networkStack == null) {
        networkStack = VolleyNetworkStack.newInstance(getContext(), getWaspHttpStack());
      }
      return networkStack;
    }

    public Wasp build() {
      return new Wasp(this);
    }
  }
}
