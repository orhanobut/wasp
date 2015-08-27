package com.orhanobut.wasp;

import android.app.Activity;
import android.content.Context;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class BaseTestCase extends TestCase {

  protected final Context context;

  public BaseTestCase() {
    context = Robolectric.buildActivity(Activity.class).create().get();
  }

  @Test
  public void test() {
    assertTrue(true);
  }
}
