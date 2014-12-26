package com.orhanobut.wasp;

import com.orhanobut.wasp.http.Body;
import com.orhanobut.wasp.http.Header;
import com.orhanobut.wasp.http.Path;
import com.orhanobut.wasp.http.Query;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Orhan Obut
 */
final class WaspRequest {

    private final String url;
    private final String method;
    private final Map<String, String> headers;
    private final String body;

    private WaspRequest(String url, String method, Map<String, String> headers, String body) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = body;
    }

    String getUrl() {
        return url;
    }

    String getMethod() {
        return method;
    }

    Map<String, String> getHeaders() {
        return headers != null ? headers : Collections.<String, String>emptyMap();
    }

    String getBody() {
        return body;
    }

    byte[] getBodyAsBytes() {
        if (body == null) {
            return null;
        }
        return body.getBytes();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Request URL : ").append(url);
        if (body != null) {
            builder.append(", Body: ").append(body);
        }
        if (!getHeaders().isEmpty()) {
            //TODO add header output
        }
        return builder.toString();
    }

    static class Builder {
        private final MethodInfo methodInfo;
        private final String baseUrl;
        private final Object[] args;
        private final Parser parser;

        private String body;
        private String relativeUrl;
        private StringBuilder queryParamBuilder;
        private Map<String, String> headers;

        Builder(MethodInfo methodInfo, Object[] args, String baseUrl, Parser parser) {
            this.methodInfo = methodInfo;
            this.baseUrl = baseUrl;
            this.args = args;
            this.parser = parser;
            this.relativeUrl = methodInfo.getRelativeUrl();

            initParams();
        }

        private void initParams() {
            Annotation[] annotations = methodInfo.getMethodAnnotations();
            int count = annotations.length;

            for (int i = 0; i < count; i++) {
                Object value = args[i];
                if (value == null) {
                    throw new NullPointerException("Value cannot be null");
                }
                Annotation annotation = annotations[i];
                if (annotation == null) {
                    continue;
                }
                Class<? extends Annotation> annotationType = annotation.annotationType();
                if (annotationType == Path.class) {
                    String key = ((Path) annotation).value();
                    addPathParam(key, (String) value);
                    continue;
                }
                if (annotationType == Query.class) {
                    String key = ((Query) annotation).value();
                    addQueryParam(key, value);
                    continue;
                }
                if (annotationType == Header.class) {
                    String key = ((Query) annotation).value();
                    addHeaderParam(key, (String) value);
                    continue;
                }
                if (annotationType == Body.class) {
                    body = getBody(value);
                }
            }
        }

        WaspRequest build() {
            return new WaspRequest(getUrl(), methodInfo.getHttpMethod(), headers, body);
        }

        private String getBody(Object body) {
            return parser.toJson(body);
        }

        private String getUrl() {
            return baseUrl + relativeUrl + getQueryString();
        }

        private String getQueryString() {
            if (queryParamBuilder == null) {
                return "";
            }
            return queryParamBuilder.toString();
        }

        private void addPathParam(String key, String value) {
            relativeUrl = relativeUrl.replace("{" + key + "}", value);
        }

        private void addQueryParam(String key, Object value) {
            StringBuilder builder = this.queryParamBuilder;
            if (queryParamBuilder == null) {
                this.queryParamBuilder = builder = new StringBuilder();
            }
            builder.append(queryParamBuilder.length() == 0 ? "?" : "&");
            builder.append(key).append("=").append(value);
        }

        private void addHeaderParam(String key, String value) {
            Map<String, String> headers = this.headers;
            if (headers == null) {
                this.headers = headers = new LinkedHashMap<>();
            }
            headers.put(key, value);
        }
    }
}
