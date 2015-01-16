package com.orhanobut.wasp;

import android.content.Context;
import android.text.TextUtils;

import com.orhanobut.wasp.utils.CollectionUtils;
import com.orhanobut.wasp.utils.IOUtils;
import com.orhanobut.wasp.utils.JsonUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Orhan Obut
 */
class MockFactory implements NetworkStack {

    private static final int MOCK_LIST_COUNT = 5;

    private static MockFactory mockFactory;

    private Context context;

    private MockFactory(final Context context) {
        // no instance
        this.context = context;
    }

    static MockFactory getDefault(Context context) {
        if (mockFactory == null) {
            mockFactory = new MockFactory(context);
        }
        return mockFactory;
    }

    @Override
    public <T> void invokeRequest(WaspRequest waspRequest, CallBack<T> callBack) {
        WaspMock mock = waspRequest.getMock();
        int statusCode = mock.getStatusCode();

        if (statusCode < 200 || statusCode > 299) {
            callBack.onError(new WaspError("mock url", "Mock test fail", statusCode));
            return;
        }

        MethodInfo methodInfo = waspRequest.getMethodInfo();
        String responseString;

        if (TextUtils.isEmpty(mock.getPath())) {
            //Create mock object and return
            Type responseType = methodInfo.getResponseObjectType();
            responseString = createJsonString(responseType);
        } else {
            try {
                responseString = IOUtils.readFileFromAssets(context, mock.getPath());
                if (TextUtils.isEmpty(responseString) || !JsonUtil.validJson(responseString)) {
                    throw new RuntimeException("Given file for \"" +
                            methodInfo.getMethod().getDeclaringClass().getSimpleName() + "." +
                            methodInfo.getMethod().getName() + "\" is either empty or contains an invalid json"
                    );
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        callBack.onSuccess((T) responseString);
    }

    /**
     * Create a json string for response. If the response object type contains parameterized types,
     * * it will recursively create these content as well.
     *
     * @param type is the response object type
     * @return json string
     */
    private String createJsonString(Type type) {
        if (ParameterizedType.class.isAssignableFrom(type.getClass())) {
            return createJsonFromParameterizedType(type);
        }
        return createJsonObjectString(type);
    }

    /**
     * It is called when the field type is parameterized type. If the type is instance of List,
     * * it will create mock object for this type as well.
     *
     * @param type is the field type that is passed
     * @return json string
     */
    private String createJsonFromParameterizedType(Type type) {
        Class rawType = (Class) ((ParameterizedType) type).getRawType();
        Type[] genericTypes = ((ParameterizedType) type).getActualTypeArguments();

        Class clazz = null;
        try {
            clazz = Class.forName(((Class) genericTypes[0]).getName());
        } catch (ClassNotFoundException e) {
            Logger.e(e.getMessage());
        }

        if (List.class.isAssignableFrom(rawType)) {
            return createJsonArrayString(clazz);
        }

        return null;
    }

    /**
     * It is called when the field type is instance of List
     *
     * @param type is the field type
     * @return json string
     */
    private String createJsonArrayString(Type type) {
        Class clazz = null;
        try {
            clazz = Class.forName(((Class) type).getName());
        } catch (ClassNotFoundException e) {
            Logger.e(e.getMessage());
        }

        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < MOCK_LIST_COUNT; i++) {
            String jsonString = createJsonObjectString(clazz);
            builder.append(jsonString);
            if (i != MOCK_LIST_COUNT - 1) {
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * It is called when the field type is an object, it iterates through all fields and creates
     * * appropriate json string
     *
     * @param type is the object type
     * @return json string
     */
    private String createJsonObjectString(Type type) {
        Class clazz = null;
        try {
            clazz = Class.forName(((Class) type).getName());
        } catch (ClassNotFoundException e) {
            Logger.e(e.getMessage());
        }

        Map<String, Object> map = new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            Class<?> cls = field.getType();
            if (String.class.isAssignableFrom(cls)) {
                map.put(field.getName(), "test");
            } else if (int.class.isAssignableFrom(cls) || Integer.class.isAssignableFrom(cls)) {
                map.put(field.getName(), 10);
            } else if (float.class.isAssignableFrom(cls) || Float.class.isAssignableFrom(cls)) {
                map.put(field.getName(), 10);
            } else if (double.class.isAssignableFrom(cls) || Double.class.isAssignableFrom(cls)) {
                map.put(field.getName(), 10);
            } else if (long.class.isAssignableFrom(cls) || Long.class.isAssignableFrom(cls)) {
                map.put(field.getName(), 10L);
            } else if (boolean.class.isAssignableFrom(cls) || Boolean.class.isAssignableFrom(cls)) {
                map.put(field.getName(), true);
            } else if (BigDecimal.class.isAssignableFrom(cls)) {
                map.put(field.getName(), 10);
            } else if (List.class.isAssignableFrom(field.getType())) {
                Type[] genericTypes = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
                Type genericType = genericTypes[0];
                map.put(field.getName(), createJsonArrayString(genericType));
            } else if (!cls.isArray()) {
                map.put(field.getName(), createJsonObjectString(cls));
            }
        }
        return CollectionUtils.toJson(map);
    }

}
