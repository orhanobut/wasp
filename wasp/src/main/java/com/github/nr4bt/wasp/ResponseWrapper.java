package com.github.nr4bt.wasp;

/**
 * @author Orhan Obut
 */
final class ResponseWrapper<T> {

    private final CallBack<T> callBack;
    private final T response;

    public ResponseWrapper(CallBack<T> callBack, T response) {
        this.callBack = callBack;
        this.response = response;
    }

    void submitResponse() {
        callBack.onSuccess(response);
    }
}
