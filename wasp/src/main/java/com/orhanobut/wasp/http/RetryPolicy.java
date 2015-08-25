package com.orhanobut.wasp.http;

import com.orhanobut.wasp.utils.WaspRetryPolicy;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface RetryPolicy {
  int timeout() default WaspRetryPolicy.DEFAULT_TIMEOUT_MS;

  int maxNumRetries() default WaspRetryPolicy.DEFAULT_MAX_RETRIES;

  float backoffMultiplier() default WaspRetryPolicy.DEFAULT_BACKOFF_MULT;
}
