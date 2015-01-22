package com.orhanobut.wasp;

import android.content.Context;
import android.text.TextUtils;

import com.orhanobut.wasp.parsers.GsonParser;
import com.orhanobut.wasp.parsers.Parser;
import com.orhanobut.wasp.utils.LogLevel;
import com.orhanobut.wasp.utils.RequestInterceptor;
import com.orhanobut.wasp.utils.WaspHttpStack;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;

import javax.net.ssl.SSLSocketFactory;

/**
 * @author Orhan Obut
 */
public class Wasp {

    private static LogLevel logLevel;

    private final Builder builder;

    private Wasp(Builder builder) {
        this.builder = builder;
        logLevel = builder.logLevel;
    }

    public <T extends WaspService> T create(Class<T> service) {
        if (service == null) {
            throw new NullPointerException("service param may not be null");
        }
        if (!service.isInterface()) {
            throw new IllegalArgumentException("Only interface type is supported");
        }
        NetworkHandler handler = NetworkHandler.newInstance(service, builder);
        return (T) handler.getProxyClass();
    }

    public static LogLevel getLogLevel() {
        return logLevel;
    }

    /**
     * Initiate download and load image process
     */
    public static class Image {

        public static WaspImage.Builder from(String path) {
            if (TextUtils.isEmpty(path)) {
                throw new IllegalArgumentException("Path cannot be empty or null");
            }
            return new WaspImage.Builder().from(path);
        }

    }

    /**
     * Initiate all required information for the wasp
     */
    public static class Builder {

        private String endPointUrl;
        private LogLevel logLevel;
        private Context context;
        private Parser parser;
        private WaspHttpStack waspHttpStack;
        private RequestInterceptor requestInterceptor;
        private NetworkStack networkStack;
        private SSLSocketFactory sslSocketFactory;
        private CookieHandler cookieHandler;
        private boolean trustAllCertificates;

        public Builder(Context context) {
            if (context == null) {
                throw new NullPointerException("Context may not be null");
            }
            this.context = context;
        }

        public Builder setEndpoint(String url) {
            if (url == null || url.trim().length() == 0) {
                throw new NullPointerException("End point url may not be null");
            }
            if (url.charAt(url.length() - 1) == '/') {
                throw new IllegalArgumentException("End point should not end with \"/\"");
            }
            this.endPointUrl = url;
            return this;
        }

        public Builder setLogLevel(LogLevel logLevel) {
            if (logLevel == null) {
                throw new NullPointerException("Log level should not be null");
            }
            this.logLevel = logLevel;
            return this;
        }

        public Builder setParser(Parser parser) {
            if (parser == null) {
                throw new NullPointerException("Parser may not be null");
            }
            this.parser = parser;
            return this;
        }

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

        public Builder setRequestInterceptor(RequestInterceptor interceptor) {
            this.requestInterceptor = interceptor;
            return this;
        }

        public Builder trustCertificates() {
            if (sslSocketFactory != null) {
                throw new IllegalStateException("Only one type of trust certificate method can be used!");
            }
            this.sslSocketFactory = OkHttpStack.getTrustAllCertSslSocketFactory();
            this.trustAllCertificates = true;
            return this;
        }

        public Builder trustCertificates(int keyStoreRawResId, String keyStorePassword) {
            if (sslSocketFactory != null) {
                throw new IllegalStateException("Only one type of trust certificate method can be used!");
            }
            this.sslSocketFactory = OkHttpStack.getPinnedCertSslSocketFactory(
                    context, keyStoreRawResId, keyStorePassword
            );
            return this;
        }

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

        public Wasp build() {
            init();
            return new Wasp(this);
        }

        private void init() {
            if (parser == null) {
                parser = new GsonParser();
            }
            if (logLevel == null) {
                logLevel = LogLevel.NONE;
            }
            if (waspHttpStack == null) {
                waspHttpStack = new OkHttpStack(trustAllCertificates);
            }
            waspHttpStack.setSslSocketFactory(sslSocketFactory);
            waspHttpStack.setCookieHandler(cookieHandler);
            networkStack = VolleyNetworkStack.newInstance(context, waspHttpStack);
        }

        String getEndPointUrl() {
            return endPointUrl;
        }

        LogLevel getLogLevel() {
            return logLevel;
        }

        Context getContext() {
            return context;
        }

        Parser getParser() {
            return parser;
        }

        WaspHttpStack getWaspHttpStack() {
            return waspHttpStack;
        }

        RequestInterceptor getRequestInterceptor() {
            return requestInterceptor;
        }

        NetworkStack getNetworkStack() {
            return networkStack;
        }
    }
}
