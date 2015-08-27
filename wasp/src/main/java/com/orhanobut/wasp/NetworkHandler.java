package com.orhanobut.wasp;

import android.content.Context;

import com.orhanobut.wasp.utils.NetworkMode;
import com.orhanobut.wasp.utils.RequestInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

final class NetworkHandler implements InvocationHandler {

  private final Map<String, MethodInfo> methodInfoCache = new LinkedHashMap<>();
  private final Class<?> service;
  private final Context context;
  private final NetworkStack networkStack;
  private final String endPoint;
  private final ClassLoader classLoader;
  private final RequestInterceptor requestInterceptor;
  private final NetworkMode networkMode;

  private NetworkHandler(Class<?> service, Wasp.Builder builder) {
    this.service = service;
    this.context = builder.getContext();
    this.networkStack = builder.getNetworkStack();
    this.endPoint = builder.getEndPointUrl();
    this.requestInterceptor = builder.getRequestInterceptor();
    this.networkMode = builder.getNetworkMode();

    ClassLoader loader = service.getClassLoader();
    this.classLoader = loader != null ? loader : ClassLoader.getSystemClassLoader();
  }

  public static NetworkHandler newInstance(Class<?> service, Wasp.Builder builder) {
    return new NetworkHandler(service, builder);
  }

  private static List<Method> getMethods(Class<?> service) {
    List<Method> result = new ArrayList<>();
    //        try {
    //           // result.add(Object.class.getMethod("equals", Object.class));
    //            //  result.add(Object.class.getMethod("hashCode", Object.class));
    //            //  result.add(Object.class.getMethod("toString", Object.class));
    //        } catch (NoSuchMethodException e) {
    //            throw new AssertionError();
    //        }

    getMethodsRecursive(service, result);
    return result;
  }

  /**
   * Fills {@code proxiedMethods} with the methods of {@code interfaces} and
   * the interfaces they extend. May contain duplicates.
   */
  private static void getMethodsRecursive(Class<?> service, List<Method> methods) {
    Collections.addAll(methods, service.getDeclaredMethods());
  }

  Object getProxyClass() {
    List<Method> methods = getMethods(service);
    fillMethods(methods);

    return Proxy.newProxyInstance(classLoader, new Class[]{service}, this);
  }

  private void fillMethods(List<Method> methods) {
    for (Method method : methods) {
      MethodInfo methodInfo = MethodInfo.newInstance(context, method);
      methodInfoCache.put(method.getName(), methodInfo);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
    final MethodInfo methodInfo = methodInfoCache.get(method.getName());

    switch (methodInfo.getReturnType()) {
      case VOID:
        return invokeCallbackRequest(proxy, method, args);
      case REQUEST:
        return invokeWaspRequest(proxy, method, args);
      case OBSERVABLE:
        return invokeObservable(method, args);
      case SYNC:
        return invokeSyncRequest(method, args);
      default:
        throw new IllegalStateException(
            "Return type should be void, WaspRequest, Observable or Object"
        );
    }
  }

  private Object invokeSyncRequest(final Method method, final Object[] args) throws Exception {
    final MethodInfo methodInfo = methodInfoCache.get(method.getName());
    RequestCreator requestCreator = new RequestCreator.Builder(methodInfo, args, endPoint)
        .setRequestInterceptor(requestInterceptor)
        .build();
    requestCreator.log();
    if (networkMode == NetworkMode.MOCK && methodInfo.isMocked()) {
      return MockNetworkStack.getDefault(context).invokeRequest(requestCreator);
    }

    return networkStack.invokeRequest(requestCreator);
  }

  private Object invokeObservable(final Method method, final Object[] args) {
    final MethodInfo methodInfo = methodInfoCache.get(method.getName());

    return Observable.create(new Observable.OnSubscribe<Object>() {
      @Override
      public void call(final Subscriber<? super Object> subscriber) {
        try {
          RequestCreator requestCreator = new RequestCreator.Builder(methodInfo, args, endPoint)
              .setRequestInterceptor(requestInterceptor)
              .build();
          requestCreator.log();

          if (networkMode == NetworkMode.MOCK && methodInfo.isMocked()) {
            subscriber.onNext(MockNetworkStack.getDefault(context).invokeRequest(requestCreator));
            return;
          }

          subscriber.onNext(networkStack.invokeRequest(requestCreator));
          subscriber.onCompleted();
        } catch (Exception e) {
          subscriber.onError(e);
        }
      }
    });
  }

  private Object invokeWaspRequest(Object proxy, final Method method, final Object[] args) {
    return invokeCallbackRequest(proxy, method, args);
  }

  private Object invokeCallbackRequest(Object proxy, final Method method, final Object[] args) {
    final MethodInfo methodInfo = methodInfoCache.get(method.getName());

    RequestCreator requestCreator = new RequestCreator.Builder(methodInfo, args, endPoint)
        .setRequestInterceptor(requestInterceptor)
        .build();
    requestCreator.log();

    final WaspRequest waspRequest = new InternalWaspRequest();

    Object lastArg = args[args.length - 1];
    final Callback<?> callback = (Callback<?>) lastArg;

    InternalCallback<Response> responseWaspCallback = new InternalCallback<Response>() {
      @Override
      public void onSuccess(Response response) {
        if (waspRequest.isCancelled()) {
          Logger.i("Response not delivered because of cancelled request");
          return;
        }
        new ResponseWrapper(callback, response, response.getResponseObject()).submitResponse();
      }

      @Override
      public void onError(WaspError error) {
        error.log();
        if (waspRequest.isCancelled()) {
          Logger.i("Response not delivered because of cancelled request");
          return;
        }
        callback.onError(error);
      }
    };

    if (networkMode == NetworkMode.MOCK && methodInfo.isMocked()) {
      MockNetworkStack.getDefault(context).invokeRequest(requestCreator, responseWaspCallback);
      return waspRequest;
    }

    networkStack.invokeRequest(requestCreator, responseWaspCallback);
    return waspRequest;
  }
}
