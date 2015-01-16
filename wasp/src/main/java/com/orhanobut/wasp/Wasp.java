package com.orhanobut.wasp;

import android.content.Context;

import com.orhanobut.wasp.parsers.GsonParser;
import com.orhanobut.wasp.parsers.Parser;

import javax.net.ssl.SSLSocketFactory;

/**
 * @author Orhan Obut
 */
public class Wasp {

    private final Builder builder;

    private Wasp(Builder builder) {
        this.builder = builder;
    }

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

    public static WaspImage.Builder loadImage() {
        return new WaspImage.Builder();
    }

    public static class Builder {

        private String endPointUrl;
        private LogLevel logLevel;
        private Context context;
        private Parser parser;
        private WaspHttpStack waspHttpStack;
        private RequestInterceptor requestInterceptor;
        private NetworkStack networkStack;
        private SSLSocketFactory sslSocketFactory;

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

        public Wasp build() {
            init();
            return new Wasp(this);
        }

        private void init() {
            if (parser == null) {
                parser = new GsonParser();
            }
            if (logLevel == null) {
                logLevel = LogLevel.ALL;
            }
            if (waspHttpStack == null) {
                waspHttpStack = new OkHttpStack();
            }
            waspHttpStack.setSslSocketFactory(sslSocketFactory);
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
