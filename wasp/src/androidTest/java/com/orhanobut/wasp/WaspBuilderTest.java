package com.orhanobut.wasp;

import com.orhanobut.wasp.http.Body;
import com.orhanobut.wasp.http.GET;
import com.orhanobut.wasp.http.POST;
import com.orhanobut.wasp.http.Path;
import com.orhanobut.wasp.http.Query;

import junit.framework.Assert;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Orhan Obut
 */
public class WaspBuilderTest extends BaseTest {

    ServiceTest service;

    interface ServiceTest {
        @GET("/repos/{user}/{repo}")
        void fetchRepo(@Path("user") String user,
                       @Path("repo") String repo,
                       CallBack<Repo> callBack
        );

        @GET("/users/{user}/repos")
        void fetchRepoBySearch(@Path("user") String user,
                               @Query("page") int pageNumber,
                               @Query("sort") String sort,
                               CallBack<List<Repo>> callBack
        );

        @POST("/repos/{user}/{repo}")
        void addName(@Path("user") String user,
                     @Path("repo") String repo,
                     @Body String body,
                     CallBack<Repo> callBack
        );

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        service = new Wasp.Builder(context)
                .setEndpoint("endpoint")
                .build()
                .create(ServiceTest.class);
    }

    public void test_activityShouldNotBeNull() {
        assertNotNull(context);

    }

    public void test_serviceShouldNotBeNull() throws Exception {
        assertNotNull(service);
    }

    public void test_onlyInterfaceShouldBeSupported() {
        class TestService {
        }

        try {
            new Wasp.Builder(context)
                    .setEndpoint("asdfads")
                    .build()
                    .create(TestService.class);
            Assert.fail("Only interface type is supported");
        } catch (Exception e) {
            assertThat(e.getMessage());
        }
    }

    public void test_endPointShouldNotBeNull() {
        try {
            new Wasp.Builder(context).build();
            Assert.fail("End point may not be null");
        } catch (Exception e) {
            assertThat(e.getMessage());
        }
    }

    public void test_endPointShouldNotSetNull() {
        try {
            new Wasp.Builder(context)
                    .setEndpoint(null)
                    .build();
            Assert.fail("End point may not be null");
        } catch (Exception e) {
            assertThat(e.getMessage());
        }
    }

    public void test_logLevelShouldNotSetNull() {
        try {
            new Wasp.Builder(context)
                    .setEndpoint("asdfadsf")
                    .setLogLevel(null)
                    .build();
            Assert.fail("Log level should not be null");
        } catch (Exception e) {
            assertThat(e.getMessage());
        }
    }

    public void test_createShouldNotAcceptNull() {
        try {
            new Wasp.Builder(context)
                    .setEndpoint("asdfadsf")
                    .build()
                    .create(null);
            Assert.fail("Create  should not be null");
        } catch (Exception e) {
            assertThat(e.getMessage());
        }
    }

    public void test_setParserShoultNotAcceptNull() {
        try {
            new Wasp.Builder(context)
                    .setParser(null)
                    .build();
            Assert.fail("Parser should not be null");
        } catch (Exception e) {
            assertThat(e.getMessage());
        }
    }

    public void test_endPointShouldNotEndWithSlash() {
        try {
            new Wasp.Builder(context)
                    .setEndpoint("asdfasd/")
                    .build();
            Assert.fail("End point should not end with /");
        } catch (Exception e) {
            assertThat(e.getMessage());
        }
    }

    public void test_contextShouldNotBeNull() {
        try {
            new Wasp.Builder(null)
                    .setEndpoint("asdfasd")
                    .build();
            Assert.fail("Context should not be null");
        } catch (Exception e) {
            assertThat(e.getMessage());
        }
    }

}
