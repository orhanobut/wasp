package com.orhanobut.wasp;

import android.text.TextUtils;

import com.orhanobut.wasp.utils.LogLevel;

import java.lang.reflect.Type;

@SuppressWarnings("unused")
public class WaspError extends Throwable {

  private final Response response;
  private final String errorMessage;
  private final LogLevel logLevel;

  public WaspError(Response response, String errorMessage) {
    this.response = response;
    this.errorMessage = errorMessage;
    this.logLevel = Wasp.getLogLevel();
  }

  /**
   * Error message coming from network layer.
   */
  public String getErrorMessage() {
    if (errorMessage == null) {
      return "";
    }
    return errorMessage;
  }

  /**
   * Response object containing status code, headers, body, etc.
   */
  public Response getResponse() {
    return response;
  }

  /**
   * HTTP response body parsed via provided {@code type}. {@code null} if there is no response
   * or no body.
   *
   * @throws RuntimeException if unable to convert the body to the provided {@code type}.
   */
  public Object getBodyAs(Type type) {
    if (response == null) {
      return null;
    }
    String body = response.getBody();
    if (TextUtils.isEmpty(body)) {
      return null;
    }
    try {
      return Wasp.getParser().fromBody(response.getBody(), type);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Wasp Error: ");
    if (errorMessage != null) {
      builder.append("Message: ")
          .append(errorMessage);
    }
    builder.append(" Status Code: ")
        .append(response.getStatusCode())
        .append(" Url ")
        .append(response.getUrl());
    return builder.toString();
  }

  void log() {
    switch (logLevel) {
      case FULL:
        // Fall Through
      case FULL_REST_ONLY:
        Logger.d("<--- ERROR");
        Logger.d("Message - " + "[" + errorMessage + "]");
        response.log();
        break;
      default:
        break;
    }
  }
}
