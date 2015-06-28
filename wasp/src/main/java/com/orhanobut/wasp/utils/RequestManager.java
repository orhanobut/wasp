package com.orhanobut.wasp.utils;

import com.orhanobut.wasp.WaspRequest;

/**
 * @author Orhan Obut
 */
public interface RequestManager {

  void addRequest(WaspRequest request);

  void cancelAll();

}
