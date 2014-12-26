package com.orhanobut.wasp;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Orhan Obut
 */
final class NetworkHandler implements InvocationHandler {

    private static final String TAG = NetworkHandler.class.getSimpleName();

    private final Map<String, MethodInfo> methodInfoCache = new LinkedHashMap<>();
    private final Class<?> service;
    private final NetworkStack networkStack;
    private final Parser parser;
    private final String endPoint;

    private ClassLoader loader;

    private NetworkHandler(ClassLoader loader, Class<?> service, NetworkStack networkStack, Parser parser,
                           String endPoint) {
        this.loader = loader;
        this.service = service;
        this.networkStack = networkStack;
        this.parser = parser;
        this.endPoint = endPoint;
    }

    static Object newInstance(ClassLoader classLoader, Class<?> service, NetworkStack networkStack, Parser parser,
                              String endPoint) {
        NetworkHandler networkHandler = new NetworkHandler(classLoader, service, networkStack, parser, endPoint);
        return networkHandler.getProxyClass();
    }

    private Object getProxyClass() {
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        if (service == null) {
            throw new NullPointerException("Service may not be null");
        }

        List<Method> methods = getMethods(service);
        fillMethods(methods);

        return Proxy.newProxyInstance(loader, new Class[]{service}, this);
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

    private void fillMethods(List<Method> methods) {
        for (Method method : methods) {
            MethodInfo methodInfo = MethodInfo.newInstance(method);
            methodInfoCache.put(method.getName(), methodInfo);
        }
    }

    @Override
    public Object invoke(Object proxy, final Method method, Object[] args) throws Throwable {
        Log.d(TAG, "method invoked");
        if (args.length == 0) {
            throw new IllegalArgumentException("Callback must be sent as param");
        }
        Object lastArg = args[args.length - 1];
        if (!(lastArg instanceof CallBack)) {
            throw new IllegalArgumentException("Last param must be type of CallBack<T>");
        }
        final CallBack<?> callBack = (CallBack<?>) lastArg;
        final MethodInfo methodInfo = methodInfoCache.get(method.getName());

        WaspRequest waspRequest = new WaspRequest.Builder(methodInfo, args, endPoint, parser).build();
        Logger.d(waspRequest.toString());
        networkStack.invokeRequest(waspRequest, new CallBack<String>() {
            @Override
            public void onSuccess(String content) {
                Logger.d("Response: " + content);
                Object result = parser.fromJson(content, methodInfo.getResponseObjectType());
                new ResponseWrapper(callBack, result).submitResponse();
            }

            @Override
            public void onError(WaspError error) {
                Logger.d(error.toString());
                callBack.onError(error);
            }
        });
        return null;
    }
}
