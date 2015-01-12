package com.orhanobut.wasp;

/**
 * @author Orhan Obut
 */
public class WaspError {

    public static final int INVALID_STATUS_CODE = -1;

    private final String errorMessage;
    private final int statusCode;
    private final String url;

    public WaspError(String url, String errorMessage, int statusCode) {
        this.url = url;
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
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
}
