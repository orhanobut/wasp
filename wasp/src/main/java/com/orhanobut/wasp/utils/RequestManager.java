package com.orhanobut.wasp.utils;

import com.orhanobut.wasp.WaspRequest;

public interface RequestManager {

  void addRequest(WaspRequest request);

  void cancelAll();

}
