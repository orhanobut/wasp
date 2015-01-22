package com.orhanobut.wasp;

import android.content.Context;

import com.orhanobut.wasp.parsers.Parser;
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

    private static final String TAG = NetworkHandler.class.getSimpleName();

    private final Map<String, MethodInfo> methodInfoCache = new LinkedHashMap<>();
    private final Class<?> service;
    private final Context context;
    private final NetworkStack networkStack;
    private final Parser parser;
    private final String endPoint;
    private final ClassLoader classLoader;
    private final RequestInterceptor requestInterceptor;

    private NetworkHandler(Class<?> service, Wasp.Builder builder) {
        this.service = service;
        this.context = builder.getContext();
        this.networkStack = builder.getNetworkStack();
        this.parser = builder.getParser();
        this.endPoint = builder.getEndPointUrl();
        this.requestInterceptor = builder.getRequestInterceptor();

        ClassLoader loader = service.getClassLoader();
        this.classLoader = loader != null ? loader : ClassLoader.getSystemClassLoader();
    }

    public static NetworkHandler newInstance(Class<?> service, Wasp.Builder builder) {
        return new NetworkHandler(service, builder);
    }

    Object getProxyClass() {
        List<Method> methods = getMethods(service);
        fillMethods(methods);

        return Proxy.newProxyInstance(classLoader, new Class[]{service}, this);
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
            MethodInfo methodInfo = MethodInfo.newInstance(context, method);
            methodInfoCache.put(method.getName(), methodInfo);
        }
    }

    @Override
    public Object invoke(Object proxy, final Method method, Object[] args) throws Throwable {
        Logger.d("Proxy method invoked");

        if(method.getName().equals("getNetworkStack")){
            Logger.d("getNetworkStack invoked");
            return networkStack;
        }

        if (args.length == 0) {
            throw new IllegalArgumentException("Callback must be sent as param");
        }
        Object lastArg = args[args.length - 1];
        if (!(lastArg instanceof CallBack)) {
            throw new IllegalArgumentException("Last param must be type of CallBack<T>");
        }
        final CallBack<?> callBack = (CallBack<?>) lastArg;
        final MethodInfo methodInfo = methodInfoCache.get(method.getName());

        WaspRequest waspRequest = new WaspRequest.Builder(methodInfo, args, endPoint, parser)
                .setRequestInterceptor(requestInterceptor)
                .build();
        Logger.d(waspRequest.toString());

        CallBack<String> responseCallBack = new CallBack<String>() {
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
        };

        if (methodInfo.isMocked()) {
            MockFactory.getDefault(context).invokeRequest(waspRequest, responseCallBack);
            return null;
        }

        networkStack.invokeRequest(waspRequest, responseCallBack);
        return null;
    }
}
