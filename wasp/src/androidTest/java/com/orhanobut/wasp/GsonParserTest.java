package com.orhanobut.wasp;

import com.orhanobut.wasp.parsers.GsonParser;
import com.orhanobut.wasp.parsers.Parser;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Orhan Obut
 */
public class GsonParserTest extends BaseTest {

    Parser parser = new GsonParser();

    public void testToJson() {
        class Foo {
            String a;
        }

        String json = parser.toBody(new Foo());
        assertThat(json).isNotNull();

    }
}
