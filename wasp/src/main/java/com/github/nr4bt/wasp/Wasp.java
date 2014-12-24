package com.github.nr4bt.wasp;

import android.content.Context;

/**
 * @author Orhan Obut
 */
public class Wasp {

    private final NetworkStack networkStack;
    private final Parser parser;
    private final String endPoint;

    private Wasp(Builder builder) {
        //currently only volley is supported
        networkStack = VolleyNetworkStack.newInstance(builder.context);
        parser = builder.parser;
        endPoint = builder.endPointUrl;
    }

    public <T> T create(Class<T> service) {
        if (service == null) {
            throw new NullPointerException("service param may not be null");
        }
        if (!service.isInterface()) {
            throw new IllegalArgumentException("Only interface type is supported");
        }
        return (T) NetworkHandler.newInstance(service.getClassLoader(), service, networkStack, parser, endPoint);
    }

    public static class Builder {

        private String endPointUrl;
        private LogLevel logLevel;
        private Context context;
        private Parser parser;

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
        }
    }
}
