package com.orhanobut.waspsample;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.orhanobut.wasp.utils.IOUtils;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Orhan Obut
 */
public class IoUtilsTest extends InstrumentationTestCase {

    Context context;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        System.setProperty(
                "dexmaker.dexcache",
                getInstrumentation().getTargetContext().getCacheDir().getPath());

        context = getInstrumentation().getContext();

    }

    public void testAssetFileExists() {
        assertThat(IOUtils.assetsFileExists(context, null)).isFalse();
    }

    public void testReadFileFromAssetsNull() throws IOException {
        assertThat(IOUtils.readFileFromAssets(context, null)).isNull();
    }


}
