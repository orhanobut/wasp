package com.orhanobut.wasp;

import android.app.Activity;
import android.content.Context;

import com.orhanobut.wasp.http.Mock;
import com.orhanobut.wasp.http.POST;
import com.orhanobut.wasp.utils.NetworkMode;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import rx.Observable;
import rx.Observer;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MethodInfoTest {

  private final Context context;

  public MethodInfoTest() {
    this.context = Robolectric.buildActivity(Activity.class).create().get();
  }

  interface Service {
    @POST("/")
    List<String> getList(Callback<String> callback);

    @POST("/")
    Observable<String> getObservable();

    @POST("/")
    @Mock
    WaspRequest getWaspRequest(Callback<String> callback);
  }

  @Test
  public void parseReturnType() {
    new Wasp.Builder(context)
        .setEndpoint("http://www")
        .build()
        .create(Service.class);
  }

  @Test
  public void testReturnType() {
    Service service = new Wasp.Builder(context)
        .setEndpoint("http://www")
        .setNetworkMode(NetworkMode.MOCK)
        .build()
        .create(Service.class);

    service.getObservable()
        .subscribe(new Observer<String>() {
          @Override
          public void onCompleted() {
            String a = "b";
          }

          @Override
          public void onError(Throwable e) {
            String a = "b";
          }

          @Override
          public void onNext(String s) {
            String a = "b";
          }
        });
  }

}
