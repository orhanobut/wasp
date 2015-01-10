package com.orhanobut.waspsample;

/**
 * @author Orhan Obut
 */
public class Ip {

    String origin;
    String foo;

    Ip(String origin, String foo) {
        this.origin = origin;
        this.foo = foo;
    }

    @Override
    public String toString() {
        return "origin:" + origin;
    }
}
