package com.orhanobut.wasp;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.orhanobut.wasp.parsers.GsonParser;
import com.orhanobut.wasp.parsers.Parser;
import com.orhanobut.wasp.utils.LogLevel;
import com.orhanobut.wasp.utils.NetworkMode;
import com.orhanobut.wasp.utils.RequestInterceptor;
import com.orhanobut.wasp.utils.SimpleRequestInterceptor;
import com.orhanobut.wasp.utils.WaspHttpStack;
import com.squareup.okhttp.OkHttpClient;

import org.junit.Test;
import org.robolectric.Robolectric;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.CookieHandler;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class WaspBuilderTest extends BaseTestCase {

  @Test
  public void testConstructorWithNullContext() throws Exception {
    try {
      new Wasp.Builder(null);
      fail("context should not be null");
    } catch (Exception e) {
      assertThat(e).hasMessage("Context should not be null");
    }
  }

  @Test
  public void testConstructorWithApplicationContext() throws Exception {
    Activity activity = Robolectric.buildActivity(Activity.class).create().get();
    try {
      new Wasp.Builder(activity.getApplicationContext())
          .setEndpoint("http://www")
          .build();
      assertThat(true);
    } catch (Exception e) {
      fail("Application context should be accepted");
    }
  }

  @Test
  public void testSetEndPoint() throws Exception {
    try {
      new Wasp.Builder(context)
          .setEndpoint(null)
          .build();
      assertThat(true);
    } catch (Exception e) {
      assertThat(e).hasMessage("End point url may not be null or empty");
    }
    try {
      new Wasp.Builder(context)
          .setEndpoint("/")
          .build();
      assertThat(true);
    } catch (Exception e) {
      assertThat(e).hasMessage("End point should not end with \"/\"");
    }
  }

  @Test
  public void testSetWaspHttStack() throws Exception {
    Wasp.Builder builder = new Wasp.Builder(context);
    try {
      builder.setWaspHttpStack(null);
      fail();
    } catch (Exception e) {
      assertThat(e).hasMessage("WaspHttpStack may not be null");
    }

    WaspHttpStack httpStack = new WaspOkHttpStack();
    builder.setWaspHttpStack(httpStack);
    assertThat(builder.getWaspHttpStack()).isEqualTo(httpStack);
  }

  @Test
  public void testLogLevelDefaultNONE() throws Exception {
    Wasp.Builder builder = new Wasp.Builder(context)
        .setEndpoint("http");
    builder.build();

    //default should be NONE
    assertThat(builder.getLogLevel()).isEqualTo(LogLevel.NONE);
  }

  @Test
  public void testLogLevelCustom() throws Exception {
    Wasp.Builder builder = new Wasp.Builder(context)
        .setLogLevel(LogLevel.FULL)
        .setEndpoint("http");
    builder.build();
    assertThat(builder.getLogLevel()).isEqualTo(LogLevel.FULL);

    Wasp.Builder builder2 = new Wasp.Builder(context)
        .setLogLevel(LogLevel.FULL_IMAGE_ONLY)
        .setEndpoint("http");
    builder2.build();
    assertThat(builder2.getLogLevel()).isEqualTo(LogLevel.FULL_IMAGE_ONLY);

    Wasp.Builder builder3 = new Wasp.Builder(context)
        .setLogLevel(LogLevel.FULL_REST_ONLY)
        .setEndpoint("http");
    builder3.build();
    assertThat(builder3.getLogLevel()).isEqualTo(LogLevel.FULL_REST_ONLY);
  }

  @Test
  public void testNetworkModeDefaultLIVE() throws Exception {
    Wasp.Builder builder = new Wasp.Builder(context)
        .setEndpoint("http");
    builder.build();

    //default should be NONE
    assertThat(builder.getNetworkMode()).isEqualTo(NetworkMode.LIVE);
  }

  @Test
  public void testNetworkModeCustomMOCK() throws Exception {
    Wasp.Builder builder = new Wasp.Builder(context)
        .setEndpoint("http")
        .setNetworkMode(NetworkMode.MOCK);
    builder.build();

    //default should be NONE
    assertThat(builder.getNetworkMode()).isEqualTo(NetworkMode.MOCK);
  }

  @Test
  public void testParserDefaultGsonParser() throws Exception {
    Wasp.Builder builder = new Wasp.Builder(context)
        .setEndpoint("http");
    builder.build();

    //default should be NONE
    assertThat(builder.getParser()).isInstanceOf(GsonParser.class);
  }

  @Test
  public void testParserCustom() throws Exception {

    class MyParser implements Parser {

      @Override
      public <T> T fromBody(String content, Type type) throws IOException {
        return null;
      }

      @Override
      public String toBody(Object body) {
        return null;
      }

      @Override
      public String getSupportedContentType() {
        return null;
      }
    }

    Wasp.Builder builder = new Wasp.Builder(context)
        .setParser(new MyParser())
        .setEndpoint("http");
    builder.build();

    //default should be NONE
    assertThat(builder.getParser()).isInstanceOf(MyParser.class);
  }

  @Test
  public void testWaspHttpStackDefaultWaspOkHttpStack() throws Exception {
    Wasp.Builder builder = new Wasp.Builder(context)
        .setEndpoint("http");
    builder.build();

    //default should be NONE
    assertThat(builder.getWaspHttpStack()).isInstanceOf(WaspOkHttpStack.class);
  }

  @Test
  public void testWaspHttpStackCustom() throws Exception {

    class MyHttpStack implements WaspHttpStack {

      @Override
      public HttpStack getHttpStack() {
        return new OkHttpStack(new OkHttpClient());
      }

      @Override
      public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {

      }

      @Override
      public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {

      }

      @Override
      public void setCookieHandler(CookieHandler cookieHandler) {

      }
    }

    Wasp.Builder builder = new Wasp.Builder(context)
        .setWaspHttpStack(new MyHttpStack())
        .setEndpoint("http");
    builder.build();

    //default should be NONE
    assertThat(builder.getWaspHttpStack()).isInstanceOf(MyHttpStack.class);
  }

  @Test
  public void testRequestInterceptorDefaultNull() throws Exception {
    Wasp.Builder builder = new Wasp.Builder(context)
        .setEndpoint("http");
    builder.build();

    //default should be NONE
    assertThat(builder.getRequestInterceptor()).isNull();
  }

  @Test
  public void testRequestInterceptorCustom() throws Exception {
    RequestInterceptor interceptor = new SimpleRequestInterceptor();

    Wasp.Builder builder = new Wasp.Builder(context)
        .setRequestInterceptor(interceptor)
        .setEndpoint("http");
    builder.build();

    //default should be NONE
    assertThat(builder.getRequestInterceptor()).isNotNull();
  }

  @Test
  public void testNetworkStackDefault() throws Exception {
    Wasp.Builder builder = new Wasp.Builder(context)
        .setEndpoint("http");
    builder.build();

    //default should be NONE
    assertThat(builder.getNetworkStack()).isNotNull();
  }

  @Test
  public void testNetworkStackCustom() throws Exception {
    File cacheDir = new File(context.getCacheDir(), "volley");

    String userAgent = "volley/0";
    try {
      String packageName = context.getPackageName();
      PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
      userAgent = packageName + "/" + info.versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

    HttpStack stack;
    if (Build.VERSION.SDK_INT >= 9) {
      stack = new HurlStack();
    } else {
      // Prior to Gingerbread, HttpUrlConnection was unreliable.
      // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
      stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
    }

    Network network = new BasicNetwork(stack);

    RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network);
    queue.start();

    Wasp.Builder builder = new Wasp.Builder(context)
        .setNetworkStack(VolleyNetworkStack.newInstance(queue))
        .setEndpoint("http");
    builder.build();

    //default should be NONE
    assertThat(builder.getNetworkStack()).isNotNull();
  }

  //TODO trust certificates
  //TODO cookies
}
