package com.orhanobut.wasp.utils;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class JsonUtil {

  private JsonUtil() {
    //no instance
  }

  public static boolean isValid(String jsonSource) {
    try {
      new JsonParser().parse(jsonSource);
      return true;
    } catch (JsonParseException e) {
      return false;
    }
  }
}
