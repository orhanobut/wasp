package com.orhanobut.wasp.utils;

import java.util.Iterator;
import java.util.Map;

public class CollectionUtils {

  private CollectionUtils() {
    // no instance
  }

  public static String toJson(Map<String, Object> map) {
    StringBuilder builder = new StringBuilder();
    builder.append("{");
    Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, Object> entry = iterator.next();
      String key = entry.getKey();
      Object value = entry.getValue();

      builder.append("\"")
          .append(key)
          .append("\":");

      if (value instanceof String) {
        String temp = (String) value;
        if (!temp.startsWith("[") && !temp.startsWith("{")) {
          temp = StringUtils.wrap(temp, "\"");
        }
        builder.append(temp);
      } else {
        builder.append(entry.getValue());
      }
      if (iterator.hasNext()) {
        builder.append(",");
      }
    }
    builder.append("}");

    return builder.toString();
  }
}
