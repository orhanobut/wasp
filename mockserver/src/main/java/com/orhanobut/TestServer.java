package com.orhanobut;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.ServerRunner;

public class TestServer extends NanoHTTPD implements HttpServer {

  public TestServer() {
    super(9095);
  }

  public static void main(String[] args) {
    ServerRunner.run(TestServer.class);
  }

  @Override
  public Response serve(IHTTPSession session) {
    try {
      Thread.sleep(4000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Method method = session.getMethod();

    switch (method) {
      case GET:
        return get(session);
      case POST:
        return post(session);
      case PUT:
        return put(session);
      case DELETE:
        return delete(session);
      case HEAD:
        return head(session);
      case OPTIONS:
        return options(session);
    }
    return null;
  }

  private static final String DEFAULT_RESPONSE = "{\"name\":\"wasp\"}";

  @Override
  public Response get(IHTTPSession session) {
    return new Response(Response.Status.ACCEPTED, "application/json", DEFAULT_RESPONSE);
  }

  @Override
  public Response post(IHTTPSession session) {
    Map<String, String> files = new HashMap<>();
    try {
      session.parseBody(files);
    } catch (IOException ioe) {
      return new Response(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
    } catch (ResponseException re) {
      return new Response(re.getStatus(), MIME_PLAINTEXT, re.getMessage());
    }

    return new Response(Response.Status.ACCEPTED, "application/json", DEFAULT_RESPONSE);
  }

  @Override
  public Response put(IHTTPSession session) {
    return new Response(Response.Status.ACCEPTED, "application/json", DEFAULT_RESPONSE);
  }

  @Override
  public Response patch(IHTTPSession session) {
    return new Response(Response.Status.ACCEPTED, "application/json", DEFAULT_RESPONSE);
  }

  @Override
  public Response head(IHTTPSession session) {
    return new Response(Response.Status.ACCEPTED, "application/json", DEFAULT_RESPONSE);
  }

  @Override
  public Response delete(IHTTPSession session) {
    return new Response(Response.Status.ACCEPTED, "application/json", DEFAULT_RESPONSE);
  }

  @Override
  public Response options(IHTTPSession session) {
    return new Response(Response.Status.ACCEPTED, "application/json", DEFAULT_RESPONSE);
  }
}
