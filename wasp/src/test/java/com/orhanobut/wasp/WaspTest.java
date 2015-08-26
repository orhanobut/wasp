package com.orhanobut.wasp;

import com.android.volley.ExecutorDelivery;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.ResponseDelivery;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.orhanobut.wasp.http.Body;
import com.orhanobut.wasp.http.DELETE;
import com.orhanobut.wasp.http.GET;
import com.orhanobut.wasp.http.PATCH;
import com.orhanobut.wasp.http.POST;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.Test;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class WaspTest extends BaseTestCase {

  private static final long TIMEOUT = 4;

  private final MockWebServer server = new MockWebServer();
  private final RequestQueue requestQueue;

  Executor executor = spy(new Executor() {
    @Override
    public void execute(Runnable command) {
      command.run();
    }
  });

  public WaspTest() throws Exception {
    File cacheDir = new File(context.getCacheDir(), "volley");
    Network network = new BasicNetwork(new OkHttpStack(new OkHttpClient()));
    ResponseDelivery delivery = new ExecutorDelivery(executor);
    requestQueue = new RequestQueue(new DiskBasedCache(cacheDir), network, 4, delivery);
    requestQueue.start();

    server.start();
  }

  static class User {
    String name;
    int no;

    User(String name, int no) {
      this.name = name;
      this.no = no;
    }
  }

  interface MyApiVoid {
    @GET("/user") void getUser(Callback<User> callback);
  }

  @Test
  public void voidCallbackGetForSuccess() throws Exception {
    MockResponse response = new MockResponse()
        .addHeader("Content-Type", "application/json; charset=utf-8")
        .setBody("{\n" +
            "  \"no\": 123,\n" +
            "  \"name\": \"Hello\"\n" +
            "}");

    server.enqueue(response);

    MyApiVoid myApi = new Wasp.Builder(context)
        .setEndpoint(server.url("/v1").toString())
        .setNetworkStack(VolleyNetworkStack.newInstance(requestQueue))
        .build()
        .create(MyApiVoid.class);

    final CountDownLatch latch = new CountDownLatch(1);
    myApi.getUser(new Callback<User>() {
      @Override
      public void onSuccess(Response response, User user) {
        assertThat(response).isNotNull();
        assertThat(user).isNotNull();
        assertThat(user.name).isEqualTo("Hello");
        assertThat(user.no).isEqualTo(123);
        latch.countDown();
      }

      @Override
      public void onError(WaspError error) {
        fail(error.getErrorMessage());
      }
    });
    RecordedRequest request = server.takeRequest();
    assertThat(request.getPath()).isEqualTo("/v1/user");
    assertThat(request.getMethod()).isEqualTo("GET");


    assertTrue(latch.await(TIMEOUT, TimeUnit.SECONDS));

    verify(executor).execute(any(Runnable.class));
    verifyNoMoreInteractions(executor);
  }

  @Test
  public void voidCallbackGetForFailure() throws Exception {
    server.enqueue(new MockResponse().setStatus("404"));

    MyApiVoid myApi = new Wasp.Builder(context)
        .setEndpoint(server.url("/v1").toString())
        .setNetworkStack(VolleyNetworkStack.newInstance(requestQueue))
        .build()
        .create(MyApiVoid.class);

    final CountDownLatch latch = new CountDownLatch(1);
    myApi.getUser(new Callback<User>() {
      @Override
      public void onSuccess(Response response, User user) {
        fail("should fail");
      }

      @Override
      public void onError(WaspError error) {
        assertTrue(true);
        assertThat(error).isNotNull();
        assertThat(error.getResponse()).isNotNull();
//        assertThat(error.getResponse().getStatusCode()).isEqualTo(404);
        latch.countDown();
      }
    });
    RecordedRequest request = server.takeRequest();
    assertThat(request.getPath()).isEqualTo("/v1/user");
    assertThat(request.getMethod()).isEqualTo("GET");

    assertTrue(latch.await(TIMEOUT, TimeUnit.SECONDS));

    verify(executor).execute(any(Runnable.class));
    verifyNoMoreInteractions(executor);
  }


  interface MyApiWaspRequest {
    @GET("/user") WaspRequest getUser(Callback<User> callback);
  }

  @Test
  public void waspRequestGetForSuccess() throws Exception {
    MockResponse response = new MockResponse()
        .addHeader("Content-Type", "application/json; charset=utf-8")
        .setBody("{\n" +
            "  \"no\": 123,\n" +
            "  \"name\": \"Hello\"\n" +
            "}");

    server.enqueue(response);

    MyApiWaspRequest myApi = new Wasp.Builder(context)
        .setEndpoint(server.url("/v1").toString())
        .setNetworkStack(VolleyNetworkStack.newInstance(requestQueue))
        .build()
        .create(MyApiWaspRequest.class);

    final CountDownLatch latch = new CountDownLatch(1);
    WaspRequest waspRequest = myApi.getUser(new Callback<User>() {
      @Override
      public void onSuccess(Response response, User user) {
        assertThat(response).isNotNull();
        assertThat(user).isNotNull();
        assertThat(user.name).isEqualTo("Hello");
        assertThat(user.no).isEqualTo(123);
        latch.countDown();
      }

      @Override
      public void onError(WaspError error) {
        fail(error.getErrorMessage());
      }
    });
    assertThat(waspRequest).isNotNull();

    RecordedRequest request = server.takeRequest();
    assertThat(request.getPath()).isEqualTo("/v1/user");

    assertTrue(latch.await(TIMEOUT, TimeUnit.SECONDS));

    verify(executor).execute(any(Runnable.class));
    verifyNoMoreInteractions(executor);
  }

  @Test
  public void waspRequestGetWithCancel() throws Exception {
    MockResponse response = new MockResponse()
        .addHeader("Content-Type", "application/json; charset=utf-8")
        .setBody("{\n" +
            "  \"no\": 123,\n" +
            "  \"name\": \"Hello\"\n" +
            "}");
    response.throttleBody(1024, 4, TimeUnit.SECONDS);

    server.enqueue(response);

    MyApiWaspRequest myApi = new Wasp.Builder(context)
        .setEndpoint(server.url("/v1").toString())
        .setNetworkStack(VolleyNetworkStack.newInstance(requestQueue))
        .build()
        .create(MyApiWaspRequest.class);

    final CountDownLatch latch = new CountDownLatch(1);
    WaspRequest waspRequest = myApi.getUser(new Callback<User>() {
      @Override
      public void onSuccess(Response response, User user) {
        fail();
      }

      @Override
      public void onError(WaspError error) {
        fail(error.getErrorMessage());
      }
    });
    assertThat(waspRequest).isNotNull();

    //cancel request immediately
    waspRequest.cancel();

    RecordedRequest request = server.takeRequest();
    assertThat(request.getPath()).isEqualTo("/v1/user");

    assertFalse(latch.await(TIMEOUT, TimeUnit.SECONDS));

    verify(executor).execute(any(Runnable.class));
    verifyNoMoreInteractions(executor);
  }

  interface MyApiSync {
    @GET("/user") User getUser();
  }

  @Test
  public void syncGetForSuccess() throws Exception {
    MockResponse response = new MockResponse()
        .addHeader("Content-Type", "application/json; charset=utf-8")
        .setBody("{\n" +
            "  \"no\": 123,\n" +
            "  \"name\": \"Hello\"\n" +
            "}");

    server.enqueue(response);

    MyApiSync myApi = new Wasp.Builder(context)
        .setEndpoint(server.url("/v1").toString())
        .setNetworkStack(VolleyNetworkStack.newInstance(requestQueue))
        .build()
        .create(MyApiSync.class);

    User user = myApi.getUser();
    assertThat(user).isNotNull();
    assertThat(user.name).isEqualTo("Hello");

    RecordedRequest request = server.takeRequest();
    assertThat(request.getPath()).isEqualTo("/v1/user");

    verify(executor).execute(any(Runnable.class));
    verifyNoMoreInteractions(executor);
  }

  @Test
  public void syncGetForFailure() throws Exception {
    server.enqueue(new MockResponse().setStatus("404"));

    MyApiSync myApi = new Wasp.Builder(context)
        .setEndpoint(server.url("/v1").toString())
        .setNetworkStack(VolleyNetworkStack.newInstance(requestQueue))
        .build()
        .create(MyApiSync.class);

    try {
      myApi.getUser();
      fail();
    } catch (Exception e) {
      //TODO add WaspError
      // assertThat(e).isInstanceOf(WaspError.class);
      assertTrue(e.getMessage(), true);
    }

    RecordedRequest request = server.takeRequest();
    assertThat(request.getPath()).isEqualTo("/v1/user");

    verify(executor).execute(any(Runnable.class));
    verifyNoMoreInteractions(executor);
  }


  interface MyApiRx {
    @GET("/user") Observable<User> getUser();
  }

  @Test
  public void rxGetForSuccess() throws Exception {
    MockResponse response = new MockResponse()
        .addHeader("Content-Type", "application/json; charset=utf-8")
        .setBody("{\n" +
            "  \"no\": 123,\n" +
            "  \"name\": \"Hello\"\n" +
            "}");

    server.enqueue(response);

    MyApiRx myApi = new Wasp.Builder(context)
        .setEndpoint(server.url("/v1").toString())
        .setNetworkStack(VolleyNetworkStack.newInstance(requestQueue))
        .build()
        .create(MyApiRx.class);

    Observable<User> observable = myApi.getUser();

    assertThat(observable).isNotNull();

    final CountDownLatch latch = new CountDownLatch(1);
    observable
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.from(executor))
        .subscribe(new Observer<User>() {
          @Override
          public void onCompleted() {
            latch.countDown();
          }

          @Override
          public void onError(Throwable e) {
            fail("should fail");
          }

          @Override
          public void onNext(User user) {
            assertThat(user).isNotNull();
            assertThat(user.name).isEqualTo("Hello");
          }
        });

    RecordedRequest request = server.takeRequest();
    assertThat(request.getPath()).isEqualTo("/v1/user");

    assertTrue(latch.await(TIMEOUT, TimeUnit.SECONDS));
  }

  @Test
  public void rxGetForFailure() throws Exception {
    server.enqueue(new MockResponse().setStatus("404"));

    MyApiRx myApi = new Wasp.Builder(context)
        .setEndpoint(server.url("/v1").toString())
        .setNetworkStack(VolleyNetworkStack.newInstance(requestQueue))
        .build()
        .create(MyApiRx.class);

    Observable<User> observable = myApi.getUser();

    assertThat(observable).isNotNull();

    final CountDownLatch latch = new CountDownLatch(1);
    observable
        .subscribeOn(Schedulers.io())
        .subscribe(new Observer<User>() {
          @Override
          public void onCompleted() {
            fail();
          }

          @Override
          public void onError(Throwable e) {
            latch.countDown();
          }

          @Override
          public void onNext(User user) {
            fail("should fail");
          }
        });

    RecordedRequest request = server.takeRequest();
    assertThat(request.getPath()).isEqualTo("/v1/user");

    assertTrue(latch.await(TIMEOUT, TimeUnit.SECONDS));
  }

  interface MyApiPost {
    @POST("/user") User user(@Body User user);
  }

  @Test
  public void syncPostForSuccess() throws Exception {
    MockResponse response = new MockResponse()
        .addHeader("Content-Type", "application/json; charset=utf-8")
        .setBody("{\n" +
            "  \"no\": 123,\n" +
            "  \"name\": \"Hello\"\n" +
            "}");

    server.enqueue(response);

    MyApiPost myApi = new Wasp.Builder(context)
        .setEndpoint(server.url("/v1").toString())
        .setNetworkStack(VolleyNetworkStack.newInstance(requestQueue))
        .build()
        .create(MyApiPost.class);

    try {
      User user = myApi.user(new User("Hello", 1));
      assertThat(user).isNotNull();
    } catch (Exception e) {
      fail();
    }

    RecordedRequest request = server.takeRequest();
    assertThat(request.getPath()).isEqualTo("/v1/user");
    assertThat(request.getMethod()).isEqualTo("POST");

    verify(executor).execute(any(Runnable.class));
    verifyNoMoreInteractions(executor);
  }

  interface MyApiMethods {

    @DELETE("/") Empty delete();

    @PATCH("/") Empty patch(@Body User user);
  }

  static class Empty {
  }

  @Test
  public void testDelete() throws Exception {
    MockResponse response = new MockResponse()
        .addHeader("Content-Type", "application/json; charset=utf-8")
        .setBody("{}");

    server.enqueue(response);

    MyApiMethods myApi = new Wasp.Builder(context)
        .setEndpoint(server.url("/v1").toString())
        .setNetworkStack(VolleyNetworkStack.newInstance(requestQueue))
        .build()
        .create(MyApiMethods.class);

    myApi.delete();
    assertThat(server.takeRequest().getMethod()).isEqualTo("DELETE");
  }

  @Test
  public void testPatch() throws Exception {
    MockResponse response = new MockResponse()
        .addHeader("Content-Type", "application/json; charset=utf-8")
        .setBody("{}");

    server.enqueue(response);

    MyApiMethods myApi = new Wasp.Builder(context)
        .setEndpoint(server.url("/v1").toString())
        .setNetworkStack(VolleyNetworkStack.newInstance(requestQueue))
        .build()
        .create(MyApiMethods.class);

    myApi.patch(new User("32424", 234));
    assertThat(server.takeRequest().getMethod()).isEqualTo("PATCH");
  }

  //TODO validate arguments
  //TODO validate url
  //TODO validate headers


}
