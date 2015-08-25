package com.orhanobut.wasp;

import com.google.gson.Gson;
import com.orhanobut.wasp.parsers.GsonParser;
import com.orhanobut.wasp.parsers.Parser;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class GsonParserTest extends TestCase {

  @Test
  public void testConstructorsInvalidValues() {
    //it should not accept null
    try {
      new GsonParser(null);
      fail("GsonParser should not accept null in the constructor");
    } catch (Exception e) {
      assertThat(e).hasMessage("Gson object should not be null");
    }
  }

  @Test
  public void testDefaultConstructor() {
    Parser parser = new GsonParser();
    String result = parser.toBody(new Foo());

    Parser parser2 = new GsonParser(new Gson());
    String result2 = parser2.toBody(new Foo());

    assertThat(result).isEqualTo(result2);
  }

  @Test
  public void testFromBody() throws IOException {
    Parser parser = new GsonParser();
    String actual = "{\"name\":\"test\"}";

    Foo expected = parser.fromBody(actual, Foo.class);
    assertThat(expected).isNotNull();
    assertThat(expected.name).isEqualTo("test");
  }

  @Test
  public void testFromBodyWithInvalidValues() throws IOException {
    Parser parser = new GsonParser();
    Foo expected = parser.fromBody(null, Foo.class);
    assertThat(expected).isNull();

    try {
      parser.fromBody("test", null);
      fail("type should not be null");
    } catch (Exception e) {
      assertThat(e).hasMessage("Type should not be null");
    }
  }

  @Test
  public void testToBody() {
    Parser parser = new GsonParser();
    String actual = parser.toBody(new Foo());
    String expected = "{\"name\":\"test\"}";
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testToBodyWithInvalidValues() {
    Parser parser = new GsonParser();
    String actual = parser.toBody(null);
    assertThat(actual).isNull();
  }

  static class Foo {
    String name = "test";
  }
}
