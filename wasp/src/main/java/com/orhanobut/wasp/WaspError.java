package com.orhanobut.wasp;

import android.text.TextUtils;

import com.orhanobut.wasp.parsers.Parser;
import com.orhanobut.wasp.utils.LogLevel;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author Orhan Obut
 */
public class WaspError {

    public static final int INVALID_STATUS_CODE = -1;

    private final Parser parser;
    private final WaspResponse response;
    private final String errorMessage;

    public WaspError(Parser parser, WaspResponse response, String errorMessage) {
        this.parser = parser;
        this.response = response;
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        if (errorMessage == null) {
            return "";
        }
        return errorMessage;
    }

    public int getStatusCode() {
        return response.statusCode;
    }

    public String getBody() {
        return response.body;
    }

    public Object getBodyAs(Type type) {
        if (response == null) {
            return null;
        }
        String body = response.getBody();
        if (TextUtils.isEmpty(body)) {
            return null;
        }
        try {
            return parser.fromJson(response.body, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Wasp Error: ");
        if (errorMessage != null) {
            builder.append("Message: ").append(errorMessage);
        }
        builder.append(" Status Code: ").append(response.statusCode)
                .append(" Url ").append(response.url);
        return builder.toString();
    }

    public void logWaspError(LogLevel logLevel) {
        switch (logLevel) {
            case FULL:
                // Fall Through
            case FULL_REST_ONLY:
                Logger.d("<--- ERROR " + response.statusCode + " " + response.url);
                Logger.d("Message - " + "[" + errorMessage + "]");
                if (!response.headers.isEmpty()) {
                    for (Map.Entry<String, String> entry : response.headers.entrySet()) {
                        Logger.d("Header - [" + entry.getKey() + ": " + entry.getValue() + "]");
                    }
                }

                Logger.d(TextUtils.isEmpty(response.body) ? "Body - no body" : "Body - " + response.body);
                Logger.d("<--- END " + "(Size: " + response.length + " bytes - Network time: " +
                        response.networkTime + " ms)");
                break;
        }
    }
}
