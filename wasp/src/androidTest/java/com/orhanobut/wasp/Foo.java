package com.orhanobut.wasp;

import com.google.gson.annotations.SerializedName;

/**
 * @author Orhan Obut
 */
public class Foo {

    String url;
    String origin;
    Headers headers;
    Args args;

    public static class Headers {
        @SerializedName("Accept") String accept;
        @SerializedName("Accept-Language") String acceptLanguage;
        @SerializedName("Cookie") String cookie;
        @SerializedName("Host") String host;
        @SerializedName("Runscope-Service") String runscopeService;
    }

    public static class Args {
        String test;
        String test1;
        String test2;
    }

}

