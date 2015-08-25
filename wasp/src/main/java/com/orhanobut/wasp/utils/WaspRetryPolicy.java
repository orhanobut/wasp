package com.orhanobut.wasp.utils;

import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;

public class WaspRetryPolicy implements RetryPolicy {

  /**
   * The current timeout in milliseconds.
   */
  private int currentTimeoutMs;

  /**
   * The current retry count.
   */
  private int currentRetryCount;

  /**
   * The maximum number of attempts.
   */
  private final int maxNumRetries;

  /**
   * The backoff multiplier for the policy.
   */
  private final float backoffMultiplier;

  /**
   * The default socket timeout in milliseconds
   */
  public static final int DEFAULT_TIMEOUT_MS = 2500;

  /**
   * The default number of retries
   */
  public static final int DEFAULT_MAX_RETRIES = 1;

  /**
   * The default backoff multiplier
   */
  public static final float DEFAULT_BACKOFF_MULT = 1f;

  /**
   * Constructs a new retry policy using the default timeouts.
   */
  public WaspRetryPolicy() {
    this(DEFAULT_TIMEOUT_MS, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT);
  }

  /**
   * Constructs a new retry policy.
   *
   * @param initialTimeoutMs  The initial timeout for the policy.
   * @param maxNumRetries     The maximum number of retries.
   * @param backoffMultiplier Backoff multiplier for the policy.
   */
  public WaspRetryPolicy(int initialTimeoutMs, int maxNumRetries, float backoffMultiplier) {
    currentTimeoutMs = initialTimeoutMs;
    this.maxNumRetries = maxNumRetries;
    this.backoffMultiplier = backoffMultiplier;
  }

  /**
   * Returns the current timeout.
   */
  @Override
  public int getCurrentTimeout() {
    return currentTimeoutMs;
  }

  /**
   * Returns the current retry count.
   */
  @Override
  public int getCurrentRetryCount() {
    return currentRetryCount;
  }

  /**
   * Returns the backoff multiplier for the policy.
   */
  public float getBackoffMultiplier() {
    return backoffMultiplier;
  }

  /**
   * Prepares for the next retry by applying a backoff to the timeout.
   *
   * @param error The error code of the last attempt.
   */
  @Override
  public void retry(VolleyError error) throws VolleyError {
    currentRetryCount++;
    currentTimeoutMs += (currentTimeoutMs * backoffMultiplier);
    if (!hasAttemptRemaining()) {
      throw error;
    }
  }

  /**
   * Returns true if this policy has attempts remaining, false otherwise.
   */
  protected boolean hasAttemptRemaining() {
    return currentRetryCount <= maxNumRetries;
  }

}
