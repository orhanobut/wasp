package com.orhanobut.wasp;

import java.util.Map;

/**
 * @author Orhan Obut
 */
public class CollectionUtils {

    public static String toJson(Map<String, Object> map) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = String.valueOf(entry.getValue());

            builder.append("\"")
                    .append(key)
                    .append("\":");

            if (value instanceof String) {
                builder.append(StringUtils.wrap(value, "\""));
            } else {
                builder.append(entry.getValue());
            }
        }
        builder.append("}");

        return builder.toString();
    }
}
