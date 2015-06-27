package com.orhanobut.wasp.parsers;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.orhanobut.wasp.utils.MimeTypes;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author Orhan Obut
 */
public class GsonParser implements Parser {

  private final Gson gson;

  public GsonParser() {
    this(new Gson());
  }

  public GsonParser(Gson gson) {
    this.gson = gson;
  }

  @Override
  public <T> T fromBody(String content, Type type) throws IOException {
    if (TextUtils.isEmpty(content)) {
      return null;
    }
    return gson.fromJson(content, type);
  }

  @Override
  public String toBody(Object body) {
    return gson.toJson(body);
  }

  @Override
  public String getSupportedContentType() {
    return MimeTypes.CONTENT_JSON;
  }

}
