package com.orhanobut.wasp;

import com.orhanobut.wasp.utils.IOUtils;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Orhan Obut
 */
public class IoUtilsTest extends BaseTest {

    public void testAssetFileExists() {
        assertThat(IOUtils.assetsFileExists(context, null)).isFalse();
    }

    public void testReadFileFromAssetsNull() throws IOException {
        assertThat(IOUtils.readFileFromAssets(context, null)).isNull();
    }


}
