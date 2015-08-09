package com.orhanobut.wasp;

import com.orhanobut.wasp.utils.WaspHttpStack;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Orhan Obut
 */
public class WaspBuilderTest extends BaseTestCase {

  @Test
  public void testConstructorWithInvalidValues() {
    try {
      new Wasp.Builder(null);
      fail("context should not be null");
    } catch (Exception e) {
      assertThat(e).hasMessage("Context should not be null");
    }
  }

  @Test
  public void testSetWaspHttStack() {
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
}
