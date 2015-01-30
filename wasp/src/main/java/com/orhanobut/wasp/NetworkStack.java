package com.orhanobut.wasp;

import com.orhanobut.wasp.parsers.Parser;

/**
 * @author Orhan Obut
 */
interface NetworkStack {

    <T> void invokeRequest(WaspRequest waspRequest, CallBack<T> callBack, Parser parser);
}
