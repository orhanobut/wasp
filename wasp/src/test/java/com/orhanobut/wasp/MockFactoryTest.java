package com.orhanobut.wasp;

import com.orhanobut.wasp.utils.MockFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MockFactoryTest {

  @Test
  public void testInstantiateObject() {
    class Safe {
      Safe() {
      }
    }

    class Unsafe {
      // no constructor defined
    }

    // Safe instantiate
    Object safe = MockFactory.instantiateObject(Safe.class);
    assertThat(safe).isNotNull();

    // Unsafe instantiate
    Object unsafe = MockFactory.instantiateObject(Unsafe.class);
    assertThat(unsafe).isNotNull();
  }

  @Test
  public void testPrimitiveFields() {
    class Primitive {
      int anInt;
      float aFloat;
      double aDouble;
      boolean aBoolean;
    }

    Primitive mock = MockFactory.createMockObject(Primitive.class);
    assertThat(mock).isNotNull();

    assertThat(mock.anInt).isEqualTo(10);
    assertThat(mock.aFloat).isEqualTo(10F);
    assertThat(mock.aDouble).isEqualTo(10D);
    assertThat(mock.aBoolean).isTrue();
  }

  @Test
  public void testObjectFields() {
    class First {
      String aString;
      Long aLong;
      BigDecimal aBigDecimal;
      Second second;

      class Second {
        Third third;

        class Third {
          int anInt;
        }
      }
    }

    First first = MockFactory.createMockObject(First.class);
    assertThat(first).isNotNull();

    assertThat(first.aString).isEqualTo("test");
    assertThat(first.aLong).isEqualTo(10L);
    assertThat(first.aBigDecimal).isEqualTo(new BigDecimal(10));
    assertThat(first.second).isNotNull();
    assertThat(first.second.third).isNotNull();
    assertThat(first.second.third.anInt).isEqualTo(10);
  }

  @Test
  public void testListFields() {
    class Foo {
      List<String> aStringList;
      List<Bar> anObjectList;
      LinkedList<Integer> aLinkedList;

      class Bar {
        int anInt;
      }
    }

    Foo foo = MockFactory.createMockObject(Foo.class);
    assertThat(foo).isNotNull();

    assertThat(foo.aStringList).isNotNull();
    assertThat(foo.aStringList).isNotEmpty();
    assertThat(foo.aStringList).isInstanceOf(ArrayList.class);
    assertThat(foo.aStringList.get(0)).isEqualTo("test");

    assertThat(foo.anObjectList).isNotNull();
    assertThat(foo.anObjectList).isNotEmpty();
    assertThat(foo.anObjectList).isInstanceOf(ArrayList.class);
    assertThat(foo.anObjectList.get(0)).isInstanceOf(Foo.Bar.class);
    assertThat(foo.anObjectList.get(0).anInt).isEqualTo(10);

    assertThat(foo.aLinkedList).isNotNull();
    assertThat(foo.aLinkedList).isNotEmpty();
    assertThat(foo.aLinkedList).isInstanceOf(LinkedList.class);
    assertThat(foo.aLinkedList.get(0)).isEqualTo(10);
  }

  private static class SuperClass {
    String superString;
    int superInt;
  }

  private static class SubClass extends SuperClass {
    String subString;
    int subInt;
  }

  @Test
  public void testSuperFields() {

    SubClass mock = MockFactory.createMockObject(SubClass.class);
    assertThat(mock).isNotNull();

    assertThat(mock.subString).isEqualTo("test");
    assertThat(mock.subInt).isEqualTo(10);

    assertThat(mock.superString).isEqualTo("test");
    assertThat(mock.superInt).isEqualTo(10);
  }
}
