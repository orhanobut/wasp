package com.orhanobut.wasp.http;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used to define the an header for the request
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Header {
  String value();
}
