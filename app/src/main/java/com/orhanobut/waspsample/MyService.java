package com.orhanobut.waspsample;

import com.orhanobut.wasp.Callback;
import com.orhanobut.wasp.WaspRequest;
import com.orhanobut.wasp.http.Body;
import com.orhanobut.wasp.http.DELETE;
import com.orhanobut.wasp.http.Field;
import com.orhanobut.wasp.http.FormUrlEncoded;
import com.orhanobut.wasp.http.GET;
import com.orhanobut.wasp.http.HEAD;
import com.orhanobut.wasp.http.PATCH;
import com.orhanobut.wasp.http.POST;
import com.orhanobut.wasp.http.PUT;
import com.orhanobut.wasp.http.Path;

/**
 * @author Orhan Obut
 */
public interface MyService {

  @GET("/get")
  WaspRequest get(
      Callback<User> callback
  );

  @POST("/post")
  WaspRequest post(
      @Body User user,
      Callback<User> callback
  );

  @POST("/post/{id}")
  WaspRequest postPath(
      @Path("id") String id,
      @Body User user,
      Callback<User> callback
  );

  @PUT("/put")
  WaspRequest put(
      @Body User user,
      Callback<User> callback
  );

  @PATCH("/patch")
  WaspRequest patch(
      @Body User user,
      Callback<User> callback
  );

  @DELETE("/delete")
  WaspRequest delete(
      Callback<User> callback
  );

  @HEAD("/head")
  WaspRequest head(
      Callback<User> callback
  );

  @FormUrlEncoded
  @POST("/post")
  WaspRequest postFormUrlEncoded(
      @Field("param1") String param1,
      @Field("param2") String param2,
      Callback<User> callback
  );

}
