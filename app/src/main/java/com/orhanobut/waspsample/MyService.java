package com.orhanobut.waspsample;

import com.orhanobut.wasp.http.Body;
import com.orhanobut.wasp.http.GET;
import com.orhanobut.wasp.http.Header;
import com.orhanobut.wasp.http.Headers;
import com.orhanobut.wasp.http.POST;
import com.orhanobut.wasp.http.Path;
import com.orhanobut.wasp.http.Query;

/**
 * @author Orhan Obut
 */
public interface MyService {

    @Headers("Accept:24242")
    @GET("/repos/{user}/{repo}")
    void fetchRepo(@Header("Accept:") String accept,
                   @Path("user") String user,
                   @Path("repo") String repo,
                   RepoCallBack callBack
    );

    @Headers({
            "asdfasdf: asdfasdf",
            "234 : as234234"
    })
    @GET("/users/{user}/repos")
    void fetchRepoBySearch(@Path("user") String user,
                           @Query("page") int pageNumber,
                           @Query("sort") String sort,
                           RepoSearchCallBack callBack
    );

    @POST("/repos/{user}/{repo}")
    void addName(@Path("user") String user,
                 @Path("repo") String repo,
                 @Body String body,
                 RepoCallBack callBack
    );
}
