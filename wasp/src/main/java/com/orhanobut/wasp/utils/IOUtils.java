package com.orhanobut.wasp.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IOUtils {

  private IOUtils() {
    //no instance
  }

  public static boolean assetsFileExists(Context context, String filePath) {
    if (TextUtils.isEmpty(filePath)) {
      return false;
    }
    try {
      InputStream inputStream = context.getAssets().open(filePath);
      inputStream.close();
    } catch (IOException e) {
      return false;
    }

    return true;
  }

  public static String readFileFromAssets(Context context, String filePath) throws IOException {
    if (TextUtils.isEmpty(filePath)) {
      return null;
    }

    StringBuilder builder = new StringBuilder();

    InputStream inputStream = context.getAssets().open(filePath);
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
    String str;

    while ((str = reader.readLine()) != null) {
      builder.append(str);
    }

    reader.close();

    return builder.toString();
  }

}
