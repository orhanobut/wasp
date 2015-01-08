package com.orhanobut.wasp;

import android.content.Context;

import com.android.volley.toolbox.HttpStack;

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

    public static class Builder {

        private String endPointUrl;
        private LogLevel logLevel;
        private Context context;
        private Parser parser;
        private HttpStack httpStack;
        private RequestInterceptor requestInterceptor;
        private NetworkStack networkStack;

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

        public Builder setHttpStack(HttpStack httpStack) {
            if (httpStack == null) {
                throw new NullPointerException("HttpStack may not be null");
            }
            this.httpStack = httpStack;
            return this;
        }

        public Builder setRequestInterceptor(RequestInterceptor interceptor) {
            this.requestInterceptor = interceptor;
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
            if (endPointUrl == null) {
                throw new NullPointerException("End point may not be null");
            }
            if (httpStack == null) {
                httpStack = new OkHttpStack();
            }
            networkStack = VolleyNetworkStack.newInstance(context, httpStack);
        }

        public String getEndPointUrl() {
            return endPointUrl;
        }

        public LogLevel getLogLevel() {
            return logLevel;
        }

        public Context getContext() {
            return context;
        }

        public Parser getParser() {
            return parser;
        }

        public HttpStack getHttpStack() {
            return httpStack;
        }

        public RequestInterceptor getRequestInterceptor() {
            return requestInterceptor;
        }

        public NetworkStack getNetworkStack() {
            return networkStack;
        }
    }
}
