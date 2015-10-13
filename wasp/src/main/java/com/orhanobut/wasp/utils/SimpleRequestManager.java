package com.orhanobut.wasp.utils;

import com.orhanobut.wasp.WaspRequest;

import java.util.ArrayList;
import java.util.List;

public class SimpleRequestManager implements RequestManager {

  private final List<WaspRequest> list;

  public SimpleRequestManager() {
    this.list = new ArrayList<>();
  }

  @Override
  public synchronized void addRequest(WaspRequest request) {
    list.add(request);
  }

  @Override
  public synchronized void cancelAll() {
    for (int i = 0, size = list.size(); i < size; i++) {
      list.get(i).cancel();
    }
    list.clear();
  }
}
