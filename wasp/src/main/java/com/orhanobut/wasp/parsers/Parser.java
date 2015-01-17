package com.orhanobut.wasp.parsers;

import java.lang.reflect.Type;

/**
 * @author Orhan Obut
 */
public interface Parser {

    <T> T fromJson(String content, Type type);

    String toJson(Object body);
}
