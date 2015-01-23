package com.orhanobut.wasp.http;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Add a tag to the request so it can be cancelled if necessary
 *
 * @author Adrien Le Roy
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Tag {
}
