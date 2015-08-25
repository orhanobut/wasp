package com.orhanobut;

import fi.iki.elonen.NanoHTTPD;

public interface HttpServer {

  public NanoHTTPD.Response get(NanoHTTPD.IHTTPSession session);

  public NanoHTTPD.Response post(NanoHTTPD.IHTTPSession session);

  public NanoHTTPD.Response put(NanoHTTPD.IHTTPSession session);

  public NanoHTTPD.Response patch(NanoHTTPD.IHTTPSession session);

  public NanoHTTPD.Response head(NanoHTTPD.IHTTPSession session);

  public NanoHTTPD.Response delete(NanoHTTPD.IHTTPSession session);

  public NanoHTTPD.Response options(NanoHTTPD.IHTTPSession session);

}
