package com.orhanobut.waspsample;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.orhanobut.wasp.parsers.GsonParser;
import com.orhanobut.wasp.parsers.Parser;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Orhan Obut
 */
public class GsonParserTest extends InstrumentationTestCase {

    Context context;
    Parser parser = new GsonParser();

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        System.setProperty(
                "dexmaker.dexcache",
                getInstrumentation().getTargetContext().getCacheDir().getPath());

        context = getInstrumentation().getContext();

    }

    public void testToJson() {
        class Foo {
            String a;
        }

        String json = parser.toJson(new Foo());
        assertThat(json).isNotNull();

    }

    public void testFromJson() throws IOException {
        class Foo {
            String a;
        }

        Foo foo1 = new Foo();

        String json = parser.toJson(foo1);
        assertThat(json).isNotNull();

        Foo foo = parser.fromJson(json, Foo.class);
        assertThat(foo).isNotNull();
    }
}
