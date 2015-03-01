package com.orhanobut.wasp.parsers;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author Orhan Obut
 */
public interface Parser {

    <T> T fromJson(String content, Type type) throws IOException;

    String toJson(Object body);
}
