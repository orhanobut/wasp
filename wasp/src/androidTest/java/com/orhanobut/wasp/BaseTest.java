package com.orhanobut.wasp;

import android.content.Context;
import android.test.InstrumentationTestCase;

/**
 * @author Orhan Obut
 */
public class BaseTest extends InstrumentationTestCase {

    Context context;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        System.setProperty(
                "dexmaker.dexcache",
                getInstrumentation().getTargetContext().getCacheDir().getPath());

        context = getInstrumentation().getContext();

    }
}
