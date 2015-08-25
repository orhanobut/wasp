package com.orhanobut.wasp.http;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Enables the mocking for the network regarding to mock type
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface Mock {
  int statusCode() default 200;

  String path() default "";
}
