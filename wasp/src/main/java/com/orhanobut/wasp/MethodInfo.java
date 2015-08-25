package com.orhanobut.wasp;

import android.content.Context;
import android.text.TextUtils;

import com.orhanobut.wasp.http.Auth;
import com.orhanobut.wasp.http.Body;
import com.orhanobut.wasp.http.BodyMap;
import com.orhanobut.wasp.http.EndPoint;
import com.orhanobut.wasp.http.FormUrlEncoded;
import com.orhanobut.wasp.http.Header;
import com.orhanobut.wasp.http.Headers;
import com.orhanobut.wasp.http.Mock;
import com.orhanobut.wasp.http.Multipart;
import com.orhanobut.wasp.http.Path;
import com.orhanobut.wasp.http.Query;
import com.orhanobut.wasp.http.RestMethod;
import com.orhanobut.wasp.http.RetryPolicy;
import com.orhanobut.wasp.utils.IOUtils;
import com.orhanobut.wasp.utils.MimeTypes;
import com.orhanobut.wasp.utils.WaspRetryPolicy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

final class MethodInfo {

  private static final int HEAD_VALUE_LENGTH = 2;

  private final Context context;
  private final Method method;

  private String baseUrl;
  private String relativeUrl;
  private String httpMethod;
  private String contentType;
  private WaspRetryPolicy retryPolicy;
  private Type responseObjectType;
  private Annotation[] methodAnnotations;
  private Map<String, String> headers;
  private MockHolder mock;
  private boolean isAuthTokenEnabled;
  private ReturnType returnType;

  enum ReturnType {
    REQUEST, OBSERVABLE, SYNC, VOID
  }

  private MethodInfo(Context context, Method method) {
    this.context = context;
    this.method = method;
    init();
  }

  synchronized void init() {
    parseMethodAnnotations();
    parseReturnType();
    parseParamAnnotations();
  }

  static MethodInfo newInstance(Context context, Method method) {
    return new MethodInfo(context, method);
  }

  private void parseMethodAnnotations() {
    Annotation[] annotations = method.getAnnotations();
    for (Annotation annotation : annotations) {
      Class<? extends Annotation> annotationType = annotation.annotationType();

      if (annotationType == Headers.class) {
        String[] headers = ((Headers) annotation).value();
        if (headers == null) {
          throw new NullPointerException("HEAD value may not be null");
        }
        addHeaders(headers);
        continue;
      }

      if (annotationType == RetryPolicy.class) {
        RetryPolicy policy = (RetryPolicy) annotation;
        retryPolicy = new WaspRetryPolicy(
            policy.timeout(), policy.maxNumRetries(), policy.backoffMultiplier()
        );
        continue;
      }

      if (annotationType == EndPoint.class) {
        EndPoint endPoint = (EndPoint) annotation;
        baseUrl = endPoint.value();
        continue;
      }

      if (annotationType == Auth.class) {
        isAuthTokenEnabled = true;
        continue;
      }

      if (annotationType == Mock.class) {
        Mock mock = (Mock) annotation;

        String path = mock.path();
        if (!TextUtils.isEmpty(path) && !IOUtils.assetsFileExists(context, path)) {
          throw new RuntimeException("Could not find given file for \""
              + method.getDeclaringClass().getSimpleName() + "." + method.getName() + "\""
          );
        }
        this.mock = new MockHolder(mock.statusCode(), path);
        continue;
      }

      if (annotationType == FormUrlEncoded.class) {
        contentType = MimeTypes.CONTENT_TYPE_FORM_URL_ENCODED;
        continue;
      }

      if (annotationType == Multipart.class) {
        contentType = MimeTypes.CONTENT_TYPE_MULTIPART;
        continue;
      }

      RestMethod methodInfo = null;

      // Look for a @RestMethod annotation on the parameter annotation indicating request method.
      for (Annotation innerAnnotation : annotationType.getAnnotations()) {
        if (RestMethod.class == innerAnnotation.annotationType()) {
          methodInfo = (RestMethod) innerAnnotation;
          break;
        }
      }
      if (methodInfo == null) {
        throw new NullPointerException("method annotation may not be null");
      }
      String path;
      try {
        path = (String) annotationType.getMethod("value").invoke(annotation);
      } catch (Exception e) {
        throw methodError("Failed to extract String 'value' from @%s annotation.",
            annotationType.getSimpleName());
      }
      relativeUrl = path;
      httpMethod = methodInfo.value();
    }
  }

  private void addHeaders(String[] values) {
    for (String header : values) {
      String[] strings = header.split(":");
      if (strings.length != HEAD_VALUE_LENGTH) {
        throw new IllegalArgumentException("HEAD value must follow key : value format");
      }
      if (headers == null) {
        headers = new HashMap<>();
      }
      headers.put(strings[0], strings[1]);
    }
  }

  private void parseCallbackResponseObjectType() {
    Type[] parameterTypes = method.getGenericParameterTypes();
    if (parameterTypes.length == 0) {
      throw new IllegalArgumentException("Callback should be added as param");
    }
    Type lastArgType;
    Class<?> lastArgClass = null;

    Type typeToCheck = parameterTypes[parameterTypes.length - 1];
    lastArgType = typeToCheck;
    if (typeToCheck instanceof ParameterizedType) {
      typeToCheck = ((ParameterizedType) typeToCheck).getRawType();
    }
    if (typeToCheck instanceof Class) {
      lastArgClass = (Class<?>) typeToCheck;
    }
    if (!Callback.class.isAssignableFrom(lastArgClass)) {
      throw new IllegalArgumentException("Last param should be CallBack");
    }

    lastArgType = RetroTypes.getSupertype(
        lastArgType, RetroTypes.getRawType(lastArgType),
        Callback.class
    );
    if (lastArgType instanceof ParameterizedType) {
      responseObjectType = getParameterUpperBound((ParameterizedType) lastArgType);
    }
  }

  private void parseObservableResponseObjectType() {
    Type type = method.getGenericReturnType();
    Class rawType = RetroTypes.getRawType(type);
    Type returnType = RetroTypes.getSupertype(type, rawType, Observable.class);
    responseObjectType = getParameterUpperBound((ParameterizedType) returnType);
  }

  private void parseParamAnnotations() {
    Annotation[][] annotationArrays = method.getParameterAnnotations();
    methodAnnotations = new Annotation[annotationArrays.length];

    List<String> pathParams = new ArrayList<>();
    List<String> queryParams = new ArrayList<>();
    List<String> headerParams = new ArrayList<>();
    boolean isBodyAdded = false;

    int count = annotationArrays.length;
    for (int i = 0; i < count; i++) {
      Annotation annotationResult = null;
      for (Annotation annotation : annotationArrays[i]) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        if (annotationType == Path.class) {
          //TODO validate
          String value = ((Path) annotation).value();
          if (pathParams.contains(value)) {
            throw new IllegalArgumentException("Path name should not be duplicated");
          }
          pathParams.add(value);
        }
        if (annotationType == Body.class) {
          if (isBodyAdded) {
            throw new IllegalArgumentException("Only one body/bodyMap can be added");
          }
          isBodyAdded = true;
        }
        if (annotationType == BodyMap.class) {
          if (isBodyAdded) {
            throw new IllegalArgumentException("Only one body/bodyMap can be added");
          }
          isBodyAdded = true;
        }
        if (annotationType == Query.class) {
          //TODO validate
          String value = ((Query) annotation).value();
          if (queryParams.contains(value)) {
            throw new IllegalArgumentException("Query name should not be duplicated");
          }
          queryParams.add(value);
        }
        if (annotationType == Header.class) {
          String value = ((Header) annotation).value();
          if (headerParams.contains(value)) {
            throw new IllegalArgumentException("Header name should not be duplicated");
          }
          headerParams.add(value);
        }

        annotationResult = annotation;
      }
      methodAnnotations[i] = annotationResult;
    }
  }

  private void parseReturnType() {
    Type type = method.getGenericReturnType();
    Class clazz = RetroTypes.getRawType(type);

    // async operation with callback
    if (type == void.class) {
      returnType = ReturnType.VOID;
      parseCallbackResponseObjectType();
      return;
    }
    if (Utils.hasRxJavaOnClasspath() && Observable.class.isAssignableFrom(clazz)) {
      returnType = ReturnType.OBSERVABLE;
      parseObservableResponseObjectType();
      return;
    }
    if (WaspRequest.class.isAssignableFrom(clazz)) {
      returnType = ReturnType.REQUEST;
      parseCallbackResponseObjectType();
      return;
    }
    returnType = ReturnType.SYNC;
    responseObjectType = type;
  }

  private static Type getParameterUpperBound(ParameterizedType type) {
    Type[] types = type.getActualTypeArguments();
    for (int i = 0; i < types.length; i++) {
      Type paramType = types[i];
      if (paramType instanceof WildcardType) {
        types[i] = ((WildcardType) paramType).getUpperBounds()[0];
      }
    }
    return types[0];
  }

  private RuntimeException methodError(String message, Object... args) {
    if (args.length > 0) {
      message = String.format(message, args);
    }
    return new IllegalArgumentException(
        method.getDeclaringClass().getSimpleName() + "." + method.getName() + ": " + message);
  }

  public Method getMethod() {
    return method;
  }

  String getRelativeUrl() {
    return relativeUrl;
  }

  String getBaseUrl() {
    return baseUrl;
  }

  String getHttpMethod() {
    return httpMethod;
  }

  boolean isMocked() {
    return mock != null;
  }

  WaspRetryPolicy getRetryPolicy() {
    return retryPolicy;
  }

  Type getResponseObjectType() {
    return responseObjectType;
  }

  Annotation[] getMethodAnnotations() {
    return methodAnnotations;
  }

  Map<String, String> getHeaders() {
    return headers != null ? headers : Collections.<String, String>emptyMap();
  }

  MockHolder getMock() {
    return mock;
  }

  boolean isAuthTokenEnabled() {
    return isAuthTokenEnabled;
  }

  public String getContentType() {
    return contentType;
  }

  public ReturnType getReturnType() {
    return returnType;
  }
}
