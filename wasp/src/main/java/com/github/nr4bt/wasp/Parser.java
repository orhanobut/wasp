package com.github.nr4bt.wasp;

import java.lang.reflect.Type;

/**
 * @author Orhan Obut
 */
interface Parser {

    <T> T fromJson(String content, Type type);

    String toJson(Object body);
}
