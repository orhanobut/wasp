package com.orhanobut.wasp;

import android.net.Uri;
import android.text.TextUtils;

import com.orhanobut.wasp.http.Body;
import com.orhanobut.wasp.http.BodyMap;
import com.orhanobut.wasp.http.Field;
import com.orhanobut.wasp.http.FieldMap;
import com.orhanobut.wasp.http.Header;
import com.orhanobut.wasp.http.Path;
import com.orhanobut.wasp.http.Query;
import com.orhanobut.wasp.http.QueryMap;
import com.orhanobut.wasp.utils.AuthToken;
import com.orhanobut.wasp.utils.CollectionUtils;
import com.orhanobut.wasp.utils.LogLevel;
import com.orhanobut.wasp.utils.RequestInterceptor;
import com.orhanobut.wasp.utils.WaspRetryPolicy;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

final class RequestCreator {

  private final String url;
  private final String method;
  private final String contentType;
  private final Map<String, String> headers;
  private final Map<String, String> fieldParams;
  private final String body;
  private final WaspRetryPolicy retryPolicy;
  private final MockHolder mock;
  private final MethodInfo methodInfo;
  private final LogLevel logLevel;

  private RequestCreator(Builder builder) {
    this.url = builder.getUrl();
    this.method = builder.getHttpMethod();
    this.headers = builder.getHeaders();
    this.body = builder.getBody();
    this.retryPolicy = builder.getRetryPolicy();
    this.mock = builder.getMock();
    this.methodInfo = builder.getMethodInfo();
    this.logLevel = Wasp.getLogLevel();
    this.fieldParams = builder.getFieldParams();
    this.contentType = builder.getContentType();
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

  MockHolder getMock() {
    return mock;
  }

  WaspRetryPolicy getRetryPolicy() {
    return retryPolicy;
  }

  void log() {
    switch (logLevel) {
      case FULL:
        // Fall Through
      case FULL_REST_ONLY:
        Logger.d("---> REQUEST " + method + " " + url);
        if (!getHeaders().isEmpty()) {
          for (Map.Entry<String, String> entry : headers.entrySet()) {
            Logger.d("Header - [" + entry.getKey() + ": " + entry.getValue() + "]");
          }
        }
        Logger.d(TextUtils.isEmpty(body) ? "Body - no body" : "Body - " + body);
        Logger.d("---> END");
        break;
      default:
        // Method is called but log level is not meant to log anything
    }
  }

  MethodInfo getMethodInfo() {
    return methodInfo;
  }

  Map<String, String> getFieldParams() {
    return fieldParams != null ? fieldParams : Collections.<String, String>emptyMap();
  }

  String getContentType() {
    return contentType;
  }

  static class Builder {

    private static final String KEY_AUTH = "Authorization";

    private final MethodInfo methodInfo;
    private final String baseUrl;
    private final Object[] args;

    private String body;
    private String relativeUrl;
    private WaspRetryPolicy retryPolicy;
    private Uri.Builder queryParamBuilder;
    private Map<String, String> headers;
    private Map<String, String> fieldParams;
    private RequestInterceptor requestInterceptor;

    Builder(MethodInfo methodInfo, Object[] args, String baseUrl) {
      this.methodInfo = methodInfo;
      this.baseUrl = baseUrl;
      this.args = args;
      this.relativeUrl = methodInfo.getRelativeUrl();

      initParams();
    }

    @SuppressWarnings("unchecked")
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
          addPathParam(key, String.valueOf(value));
          continue;
        }
        if (annotationType == Query.class) {
          String key = ((Query) annotation).value();
          addQueryParam(key, value);
          continue;
        }
        if (annotationType == QueryMap.class) {
          if (!(value instanceof Map)) {
            throw new IllegalArgumentException("QueryMap accepts only Map instances");
          }
          Map<String, ?> map;
          try {
            map = (Map<String, ?>) value;
          } catch (Exception e) {
            throw new ClassCastException("QueryMap type should be Map<String, ?>");
          }
          for (Map.Entry<String, ?> entry : map.entrySet()) {
            addQueryParam(entry.getKey(), entry.getValue());
          }
          continue;
        }
        if (annotationType == Header.class) {
          String key = ((Header) annotation).value();
          addHeaderParam(key, (String) value);
          continue;
        }

        if (annotationType == Field.class) {
          String key = ((Field) annotation).value();
          addFieldParams(key, (String) value);
          continue;
        }

        if (annotationType == FieldMap.class) {
          if (!(value instanceof Map)) {
            throw new IllegalArgumentException("FieldMap accepts only Map instances");
          }
          Map<String, String> map;
          try {
            map = (Map<String, String>) value;
          } catch (Exception e) {
            throw new ClassCastException("FieldMap type should be Map<String, String>");
          }
          for (Map.Entry<String, String> entry : map.entrySet()) {
            addFieldParams(entry.getKey(), entry.getValue());
          }
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
    RequestCreator build() {
      postInit();
      return new RequestCreator(this);
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

      //Add intercepted query params
      Map<String, Object> tempQueryParams = new HashMap<>();
      requestInterceptor.onQueryParamsAdded(tempQueryParams);
      for (Map.Entry<String, Object> entry : tempQueryParams.entrySet()) {
        addQueryParam(entry.getKey(), entry.getValue());
      }

      //Add intercepted headers
      Map<String, String> tempHeaders = new HashMap<>();
      requestInterceptor.onHeadersAdded(tempHeaders);
      for (Map.Entry<String, String> entry : tempHeaders.entrySet()) {
        addHeaderParam(entry.getKey(), entry.getValue());
      }

      //If retry policy is not already set via annotations than set it via requestInterceptor
      WaspRetryPolicy waspRetryPolicy = requestInterceptor.getRetryPolicy();
      if (retryPolicy == null && waspRetryPolicy != null) {
        retryPolicy = waspRetryPolicy;
      }

      // If authToken is set, it will check if the filter is enabled
      // it will add token to each request if the filter is not enabled
      // If the filter is enabled, it will be added to request which has @Auth annotation
      AuthToken authToken = requestInterceptor.getAuthToken();
      if (authToken != null) {
        String token = authToken.getToken();
        if (!authToken.isFilterEnabled()) {
          addHeaderParam(KEY_AUTH, token);
          return;
        }
        if (methodInfo.isAuthTokenEnabled()) {
          addHeaderParam(KEY_AUTH, token);
        }
      }
    }

    private String getBody(Object body) {
      return Wasp.getParser().toBody(body);
    }

    /**
     * If endpoint is set as annotation, it uses that endpoint for the call,
     * otherwise it uses endpoint
     *
     * @return full url
     */
    private String getUrl() {
      String endpoint = methodInfo.getBaseUrl();
      if (endpoint == null) {
        endpoint = baseUrl;
      }
      return endpoint + relativeUrl + getQueryString();
    }

    private String getQueryString() {
      if (queryParamBuilder == null) {
        return "";
      }
      return queryParamBuilder.toString();
    }

    private void addPathParam(String key, String value) {
      try {
        String encodedValue = URLEncoder.encode(String.valueOf(value), "UTF-8");
        relativeUrl = relativeUrl.replace("{" + key + "}", encodedValue);
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException("unable to encode the value for path");
      }
    }

    private void addQueryParam(String key, Object value) {
      if (queryParamBuilder == null) {
        queryParamBuilder = new Uri.Builder();
      }
      queryParamBuilder.appendQueryParameter(key, String.valueOf(value));
    }

    private void addHeaderParam(String key, String value) {
      Map<String, String> headers = this.headers;
      if (headers == null) {
        headers = new LinkedHashMap<>();
        this.headers = headers;
      }
      headers.put(key, value);
    }

    /**
     * Adds the field to the map in order to use in form-url-encoded
     *
     * @param key   of the field
     * @param value of the field
     */
    private void addFieldParams(String key, String value) {
      Map<String, String> params = this.fieldParams;
      if (params == null) {
        params = new LinkedHashMap<>();
        this.fieldParams = params;
      }
      params.put(key, value);
    }

    String getHttpMethod() {
      return methodInfo.getHttpMethod();
    }

    Map<String, String> getHeaders() {
      return headers;
    }

    String getBody() {
      return body;
    }

    WaspRetryPolicy getRetryPolicy() {
      return retryPolicy;
    }

    MockHolder getMock() {
      return methodInfo.getMock();
    }

    MethodInfo getMethodInfo() {
      return methodInfo;
    }

    String getContentType() {
      if (methodInfo.getContentType() == null) {
        return Wasp.getParser().getSupportedContentType();
      }
      return methodInfo.getContentType();
    }

    Map<String, String> getFieldParams() {
      return fieldParams;
    }

  }
}
