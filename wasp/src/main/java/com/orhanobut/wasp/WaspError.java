package com.orhanobut.wasp;

import android.text.TextUtils;

import com.orhanobut.wasp.utils.LogLevel;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @author Orhan Obut
 */
public class WaspError {

    public static final int INVALID_STATUS_CODE = -1;

    private final String url;
    private final int statusCode;
    private final Map<String, String> headers;
    private final String errorMessage;
    private final byte[] body;
    private final long networkTime;

    public WaspError(String url, int statusCode, Map<String, String> headers, String errorMessage, byte[] body,
                     long networkTime) {
        this.url = url;
        this.statusCode = statusCode;
        this.headers = headers;
        this.errorMessage = errorMessage;
        this.body = body;
        this.networkTime = networkTime;
    }

    public String getErrorMessage() {
        if (errorMessage == null) {
            return "";
        }
        return errorMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Wasp Error: ");
        if (errorMessage != null) {
            builder.append(", Message:").append(errorMessage);
        }
        builder.append("Status Code: ").append(statusCode)
                .append("Url ").append(url);
        return builder.toString();
    }

    public void logWaspError(LogLevel logLevel) {
        switch (logLevel) {
            case FULL:
                // Fall Through
            case FULL_REST_ONLY:
                Logger.d("<--- ERROR " + statusCode + " " + url);
                Logger.d("Message - " + "[" + errorMessage + "]");
                if (!headers.isEmpty()) {
                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        Logger.d("Header - [" + entry.getKey() + ": " + entry.getValue() + "]");
                    }
                }

                String bodyString = "";
                try {
                    bodyString = new String(body, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    bodyString = "Unable to parse error body!!!!!";
                }
                Logger.d(TextUtils.isEmpty(bodyString) ? "Body - no body" : "Body - " + bodyString);
                Logger.d("<--- END " + "(Size: " + body.length + " bytes - Network time: " + networkTime + " ms)");
                break;
        }
    }
}
