package com.orhanobut.wasp.utils;

import java.util.LinkedHashMap;

public class WaspCache<K, V> {

  private final LinkedHashMap<K, V> map;

  public WaspCache() {
    this.map = new LinkedHashMap<>(0, 0.75f, true);
  }

  public void put(K key, V value) {
    if (key == null || value == null) {
      throw new NullPointerException("key == null || value == null");
    }
    V temp = map.get(key);
    synchronized (map) {
      if (temp != null) {
        map.remove(key);
      }
      map.put(key, value);
    }
  }

  public void remove(K key) {
    if (key == null) {
      throw new NullPointerException("key may not be null");
    }

    synchronized (map) {
      map.remove(key);
    }
  }

  public V get(K key) {
    if (key == null) {
      throw new NullPointerException("key may not be null");
    }
    return map.get(key);
  }

  public synchronized void clearAll() {
    map.clear();
  }
}
