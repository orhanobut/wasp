package com.orhanobut.wasp;

import com.orhanobut.wasp.utils.WaspCache;

import junit.framework.Assert;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by yekmer
 */
public class WaspCacheTest extends BaseTest {

    public void test_get() {
        WaspCache<String, String> waspCache = new WaspCache<>();
        waspCache.put("key", "value");
        assertEquals("value", waspCache.get("key"));
    }

    public void test_getWithNullKey() {
        WaspCache<String, String> waspCache = new WaspCache<>();
        try {
            waspCache.get(null);
        } catch (NullPointerException e) {
            assertThat(e).isNotNull();
            return;
        }
        Assert.fail("Null key did not throw NullPointerException");
    }

    public void test_getWithDifferentKeyNotEquals() {
        WaspCache<String, String> waspCache = new WaspCache<>();
        waspCache.put("key", "value");
        assertThat(waspCache.get("anotherKey")).isNotEqualTo("value");
    }

    public void test_putNullKey() {
        WaspCache<String, String> waspCache = new WaspCache<>();
        try {
            waspCache.put(null, "value");
        } catch (NullPointerException e) {
            assertThat(e).isNotNull();
            return;
        }
        Assert.fail("Null key did not throw NullPointerException");
    }

    public void test_putNullValue() {
        WaspCache<String, String> waspCache = new WaspCache<>();
        try {
            waspCache.put("key", null);
        } catch (NullPointerException e) {
            assertThat(e).isNotNull();
            return;
        }
        Assert.fail("Null key did not throw NullPointerException");
    }

    public void test_putMultiple() {
        WaspCache<String, String> waspCache = new WaspCache<>();
        waspCache.put("key1", "value1");
        waspCache.put("key2", "value2");
        assertThat(waspCache.get("key1")).isNotNull();
    }

    public void test_removeWithNullKey() {
        WaspCache<String, String> waspCache = new WaspCache<>();
        try {
            waspCache.remove(null);
        } catch (NullPointerException e) {
            assertThat(e).isNotNull();
            return;
        }
        Assert.fail("Null key did not throw NullPointerException");
    }

    public void test_remove() {
        WaspCache<String, String> waspCache = new WaspCache<>();
        waspCache.put("key", "value");
        waspCache.remove("key");
        assertThat(waspCache.get("key")).isNull();
    }

    public void test_clearAll() {
        WaspCache<String, String> waspCache = new WaspCache<>();
        waspCache.put("key", "value");
        waspCache.put("key1", "value1");
        waspCache.clearAll();
        assertThat(waspCache.get("key")).isNull();
        assertThat(waspCache.get("key1")).isNull();
    }
}

