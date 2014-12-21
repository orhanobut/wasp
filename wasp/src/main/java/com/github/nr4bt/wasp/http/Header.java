package com.github.nr4bt.wasp.http;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Make a GET request to a REST path relative to base URL
 *
 * @author Orhan Obut
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Header {
    String value();
}
