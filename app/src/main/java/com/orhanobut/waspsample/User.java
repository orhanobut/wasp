package com.orhanobut.waspsample;

/**
 * @author Orhan Obut
 */
public class User {

  String name;

  public User(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "User{" +
        "name='" + name + '\'' +
        '}';
  }
}
