package com.orhanobut.wasp;

/**
 * @author Orhan Obut
 */
public class Ip {

    String origin;
    String foo;
    int intTest;
    boolean booleanTest;
    long longTest;
    double doubleTest;

    Double doubleObjectTest;

    // List<Repo> repos;

    Repo repo;

    Ip(String origin, String foo) {
        this.origin = origin;
        this.foo = foo;
    }

    @Override
    public String toString() {
        return "origin:" + origin;
    }
}
