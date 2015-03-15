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

/**
 * @author Orhan Obut
 */
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
    public Object invoke(Object proxy, final Method method, Object[] args) throws Throwable {
        if (args.length == 0) {
            throw new IllegalArgumentException("Callback must be sent as param");
        }
        Object lastArg = args[args.length - 1];
        if (!(lastArg instanceof CallBack)) {
            throw new IllegalArgumentException("Last param must be type of CallBack<T>");
        }
        final CallBack<?> callBack = (CallBack<?>) lastArg;
        final MethodInfo methodInfo = methodInfoCache.get(method.getName());

        WaspRequest waspRequest = new WaspRequest.Builder(methodInfo, args, endPoint)
                .setRequestInterceptor(requestInterceptor)
                .build();
        waspRequest.log();

        CallBack<WaspResponse> responseCallBack = new CallBack<WaspResponse>() {
            @Override
            public void onSuccess(WaspResponse response) {
                response.log();
                new ResponseWrapper(callBack, response.getResponseObject()).submitResponse();
            }

            @Override
            public void onError(WaspError error) {
                error.log();
                callBack.onError(error);
            }
        };

        if (networkMode == NetworkMode.MOCK && methodInfo.isMocked()) {
            MockNetworkStack.getDefault(context).invokeRequest(waspRequest, responseCallBack);
            return null;
        }

        networkStack.invokeRequest(waspRequest, responseCallBack);
        return null;
    }
}
