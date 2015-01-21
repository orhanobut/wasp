package com.orhanobut.wasp;

import android.text.TextUtils;

import com.orhanobut.wasp.utils.LogLevel;

import java.util.Map;

/**
 * @author alessandro.balocco
 */
final class WaspResponse {

    private final String url;
    private final int statusCode;
    private final Map<String, String> headers;
    private final String body;
    private final int length;
    private final long delay;

    public WaspResponse(String url, int statusCode, Map<String, String> headers, String body, int length, long delay) {
        this.url = url;
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
        this.length = length;
        this.delay = delay;
    }

    public String getBody() {
        return body;
    }

    private String getFormattedBody() {
        return body.replace("\n", "").replace("\r", "").replace("\t", "");
    }

    public void logWaspResponse(LogLevel logLevel) {
        switch (logLevel) {
            case ALL:
                Logger.d("<--- RESPONSE " + statusCode + " " + url);
                if (!headers.isEmpty()) {
                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        Logger.d("Header - [" + entry.getKey() + ": " + entry.getValue() + "]");
                    }
                }
                Logger.d(TextUtils.isEmpty(body) ? "Body - no body" : "Body - " + getFormattedBody());
                Logger.d("<--- END " + "(Size: " + length + " bytes - Request time: " + delay + " ms)");
                break;
        }
    }
}
