package com.orhanobut.waspsample;

import com.github.nr4bt.wasp.http.Body;
import com.github.nr4bt.wasp.http.GET;
import com.github.nr4bt.wasp.http.POST;
import com.github.nr4bt.wasp.http.Path;
import com.github.nr4bt.wasp.http.Query;

/**
 * @author Orhan Obut
 */
public interface MyService {

    @GET("/repos/{user}/{repo}")
    void fetchRepo(@Path("user") String user,
                   @Path("repo") String repo,
                   RepoCallBack callBack
    );

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
