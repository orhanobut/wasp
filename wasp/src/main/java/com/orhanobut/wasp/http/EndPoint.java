package com.orhanobut.wasp.http;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Sets end point url
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface EndPoint {
  String value();
}
