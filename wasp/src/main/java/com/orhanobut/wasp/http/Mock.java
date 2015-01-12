package com.orhanobut.wasp.http;

import com.orhanobut.wasp.MockType;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Enables the mocking for the network regarding to mock type
 *
 * @author Orhan Obut
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface Mock {

    MockType value();
}
