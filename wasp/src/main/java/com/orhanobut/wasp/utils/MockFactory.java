package com.orhanobut.wasp.utils;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.internal.UnsafeAllocator;
import com.orhanobut.wasp.Logger;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory util for creating populated mock response objects.
 */
public final class MockFactory {

  private static final int MOCK_LIST_COUNT = 5;

  private MockFactory() {
    //no instance
  }

  /**
   * Reads mock response string from given path.
   *
   * @param context  Context with file access
   * @param filePath Path to mock file
   * @return Response string
   */
  public static String readMockResponse(Context context, String filePath) {

    String responseString;
    try {
      responseString = IOUtils.readFileFromAssets(context, filePath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    if (TextUtils.isEmpty(responseString)) {
      throw new RuntimeException("Mock file \"" + filePath + "\" is empty");
    }

    return responseString;
  }

  /**
   * Creates a mock object and populates its fields.
   *
   * @param type Type of mock object
   * @param <T>  Type of mock object
   * @return Mock Object of type T
   */
  @SuppressWarnings("unchecked") // types must agree
  public static <T> T createMockObject(Type type) {
    Class<T> rawType;

    if (type instanceof ParameterizedType) {
      rawType = (Class<T>) ((ParameterizedType) type).getRawType();
    } else if (type instanceof Class) {
      rawType = (Class<T>) type;
    } else {
      throw new UnsupportedOperationException("Unsupported type: "
          + type.getClass().getSimpleName());
    }
    T instance = instantiateObject(rawType);

    return populateObject(instance);
  }

  /**
   * Creates a new instance of given raw type.
   *
   * @param rawType Raw Type of object
   * @param <T>     Type of object
   * @return Object instance
   */
  @SuppressWarnings("unchecked") // types must agree
  public static <T> T instantiateObject(Class<T> rawType) {
    try {
      Constructor<?> constructor = rawType.getDeclaredConstructor();
      if (!constructor.isAccessible()) {
        constructor.setAccessible(true);
      }
      return (T) constructor.newInstance((Object[]) null);
    } catch (Exception e) {
      // Default constructor failed, attempt Unsafe Allocation
      Logger.w("Default constructor failed for "
          + rawType.getCanonicalName()
          + "\nWith exception : " + e.getMessage()
          + "\nAttempting unsafe allocation of object.");
    }

    try {
      UnsafeAllocator unsafeAllocator = UnsafeAllocator.create();
      return unsafeAllocator.newInstance(rawType);
    } catch (Exception e) {
      // Give up
      throw new RuntimeException("Failed to instantiate "
          + rawType.getCanonicalName(), e);
    }
  }

  /**
   * All non-final fields in object are populated with mock values.
   *
   * @param object Object to populate
   * @param <T>    Type of object
   * @return Populated object
   */
  private static <T> T populateObject(T object) {
    Class clazz = object.getClass();

    while (clazz != null) {
      String name = clazz.getName();
      if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")) {
        // Skip system classes
        break;
      }

      Field[] fields = clazz.getDeclaredFields();

      for (Field field : fields) {
        if (!Modifier.isFinal(field.getModifiers())) {
          Object value = generateValue(field);
          field.setAccessible(true);
          try {
            field.set(object, value);
          } catch (Exception e) {
            throw new RuntimeException("Failed to populate object of type "
                + object.getClass().getCanonicalName(), e);
          }
        }
      }
      clazz = clazz.getSuperclass();
    }

    return object;
  }

  /**
   * Generates a mock value assignable to provided field.
   * Parametrized types are only supported for List fields.
   * If not a list, raw type is used.
   *
   * @param field Field to generate value for
   * @return Generated value
   */
  private static Object generateValue(Field field) {
    Class<?> rawType = field.getType();

    //TODO Objects other than Lists might need parametrized types.
    if (List.class.isAssignableFrom(rawType)) {
      ParameterizedType genericType = (ParameterizedType) field.getGenericType();
      return createListObject(genericType);
    }

    return generateValue(rawType);
  }

  /**
   * Generates a mock value assignable to provided raw type.
   *
   * @param rawType Type of field
   * @return Generated value
   */
  private static Object generateValue(Class<?> rawType) {

    if (String.class.isAssignableFrom(rawType)) {
      return "test";
    } else if (int.class.isAssignableFrom(rawType) || Integer.class.isAssignableFrom(rawType)) {
      return 10;
    } else if (float.class.isAssignableFrom(rawType) || Float.class.isAssignableFrom(rawType)) {
      return 10F;
    } else if (double.class.isAssignableFrom(rawType) || Double.class.isAssignableFrom(rawType)) {
      return 10D;
    } else if (long.class.isAssignableFrom(rawType) || Long.class.isAssignableFrom(rawType)) {
      return 10L;
    } else if (boolean.class.isAssignableFrom(rawType) || Boolean.class.isAssignableFrom(rawType)) {
      return true;
    } else if (BigDecimal.class.isAssignableFrom(rawType)) {
      return new BigDecimal(10);
    } else if (!rawType.isArray()) {
      return createMockObject(rawType);
    }
    //TODO Date and BigInteger classes may be considered.

    // Field type is not supported.
    Logger.w("Unsupported field type : " + rawType.getCanonicalName());
    return null;
  }

  /**
   * Creates and fills a List object.
   * If defined as interface, creates an {@link java.util.ArrayList}.
   *
   * @param type Type of List.
   * @return List object
   */
  @SuppressWarnings("unchecked") // types must agree
  private static List createListObject(ParameterizedType type) {

    Class rawType = (Class) type.getRawType();
    Type[] genericTypes = type.getActualTypeArguments();
    List listObject;

    if (rawType.isInterface()) {
      listObject = new ArrayList(MOCK_LIST_COUNT);
    } else {
      listObject = (List) instantiateObject(rawType);
    }

    for (int i = 0; i < MOCK_LIST_COUNT; i++) {
      listObject.add(generateValue((Class) genericTypes[0]));
    }

    return listObject;
  }
}
