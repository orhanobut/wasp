package com.orhanobut.wasp;

import com.orhanobut.wasp.http.Body;
import com.orhanobut.wasp.http.BodyMap;
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
    private final WaspRetryPolicy retryPolicy;

    private WaspRequest(String url, String method, Map<String, String> headers, String body, WaspRetryPolicy retryPolicy) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = body;
        this.retryPolicy = retryPolicy;
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

    WaspRetryPolicy getRetryPolicy() {
        return retryPolicy;
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
        private WaspRetryPolicy retryPolicy;
        private StringBuilder queryParamBuilder;
        private Map<String, String> headers;
        private RequestInterceptor requestInterceptor;

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
                    String key = ((Header) annotation).value();
                    addHeaderParam(key, (String) value);
                    continue;
                }
                if (annotationType == Body.class) {
                    body = getBody(value);
                }
                if (annotationType == BodyMap.class) {
                    if (!(value instanceof Map)) {
                        throw new IllegalArgumentException("BodyMap accepts only Map instances");
                    }
                    Map<String, Object> map;
                    try {
                        map = (Map<String, Object>) value;
                    } catch (Exception e) {
                        throw new ClassCastException("Map type should be Map<String,Object>");
                    }
                    body = CollectionUtils.toJson(map);
                }
            }
        }

        Builder setRequestInterceptor(RequestInterceptor interceptor) {
            this.requestInterceptor = interceptor;
            return this;
        }

        /**
         * Merges static and param headers and create a request.
         *
         * @return WaspRequest
         */
        WaspRequest build() {
            postInit();
            return new WaspRequest(getUrl(), methodInfo.getHttpMethod(), headers, body, retryPolicy);
        }

        /**
         * It is called right before building a request, this method will add
         * intercepted headers, params, static headers and retry policy
         */
        private void postInit() {
            //Add static headers
            for (Map.Entry<String, String> entry : methodInfo.getHeaders().entrySet()) {
                addHeaderParam(entry.getKey(), entry.getValue());
            }

            //Set retry policy
            if (methodInfo.getRetryPolicy() != null) {
                retryPolicy = methodInfo.getRetryPolicy();
            }

            if (requestInterceptor == null) {
                return;
            }

            if (requestInterceptor.getQueryParams() != null) {
                //Add intercepted query params
                for (Map.Entry<String, String> entry : requestInterceptor.getQueryParams().entrySet()) {
                    addQueryParam(entry.getKey(), entry.getValue());
                }
            }
            if (requestInterceptor.getHeaders() != null) {
                //Add intercepted headers
                for (Map.Entry<String, String> entry : requestInterceptor.getHeaders().entrySet()) {
                    addHeaderParam(entry.getKey(), entry.getValue());
                }
            }
            //If retry policy is not already set via annotations than set it via requestInterceptor
            if (retryPolicy == null && requestInterceptor.getRetryPolicy() != null) {
                retryPolicy = requestInterceptor.getRetryPolicy();
            }
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
