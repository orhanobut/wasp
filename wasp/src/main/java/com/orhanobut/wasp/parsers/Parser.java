package com.orhanobut.wasp.parsers;

import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * @author Orhan Obut
 */
public interface Parser {

    <T> T fromJson(String content, Type type) throws JsonSyntaxException;

    String toJson(Object body);
}
