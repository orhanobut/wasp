package com.orhanobut.wasp;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InternalWaspRequestTest extends BaseTestCase {

  @Test
  public void testIsCanceled() {
    WaspRequest request = new InternalWaspRequest();
    assertThat(request.isCancelled()).isFalse();

    request.cancel();
    assertThat(request.isCancelled()).isTrue();
  }
}
