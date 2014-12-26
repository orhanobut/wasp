package com.orhanobut.waspsample;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.orhanobut.wasp.CallBack;
import com.orhanobut.wasp.Wasp;
import com.orhanobut.wasp.http.GET;
import com.orhanobut.wasp.http.Path;

import junit.framework.Assert;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Orhan Obut
 */
public class MyServiceTest extends InstrumentationTestCase {

    private static final String TAG = MyServiceTest.class.getSimpleName();
    
    Context context;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        System.setProperty(
                "dexmaker.dexcache",
                getInstrumentation().getTargetContext().getCacheDir().getPath());

        context = getInstrumentation().getContext();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        context = null;
    }

    public interface Service1 {
        @GET("/repos/{user}/{repo}")
        void fetchRepo(@Path("user") String user,
                       @Path("repo") String repo
        );
    }

    public void test_methodShouldHaveCallBackParam() {
        try {
            new Wasp.Builder(context)
                    .setEndpoint("endpoint")
                    .build()
                    .create(Service1.class);
            Assert.fail("Last param should be callback");
        } catch (Exception e) {
            assertThat(e.getMessage());
        }
    }
    
    public interface Service2 {
        @GET("/repos/{user}/{repo}")
        void fetchRepo(@Path("user") String user,
                       @Path("user") String repo,
                       CallBack callBack
        );
    }

    public void test_pathValueShouldNotBeDuplicated() {
        try {
            new Wasp.Builder(context)
                    .setEndpoint("endpoint")
                    .build()
                    .create(Service2.class);
            Assert.fail("Path value should not be duplicated");
        } catch (Exception e) {
            assertThat(e.getMessage());
        }
    }

    public interface Service3 {
        @GET("/repos/{user}/{repo}")
        void fetchRepo(@Path("user") String user,
                       CallBack callBack,
                       @Path("repo") String repo
        );
    }

    public void test_callBackShouldBeLastParam() {
        try {
            new Wasp.Builder(context)
                    .setEndpoint("endpoint")
                    .build()
                    .create(Service3.class);
            Assert.fail("Callback should be last param");
        } catch (Exception e) {
            assertThat(e.getMessage());
        }
    }
}
