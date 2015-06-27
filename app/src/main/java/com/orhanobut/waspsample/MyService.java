package com.orhanobut.waspsample;

import com.orhanobut.wasp.MyCallBack;
import com.orhanobut.wasp.http.Body;
import com.orhanobut.wasp.http.DELETE;
import com.orhanobut.wasp.http.Field;
import com.orhanobut.wasp.http.FormUrlEncoded;
import com.orhanobut.wasp.http.GET;
import com.orhanobut.wasp.http.HEAD;
import com.orhanobut.wasp.http.PATCH;
import com.orhanobut.wasp.http.POST;
import com.orhanobut.wasp.http.PUT;

/**
 * @author Orhan Obut
 */
public interface MyService {

  @GET("/get")
  void get(
      MyCallBack<User> callback
  );

  @POST("/post")
  void post(
      @Body User user,
      MyCallBack<User> callback
  );

  @PUT("/put")
  void put(
      @Body User user,
      MyCallBack<User> callback
  );


  @PATCH("/patch")
  void patch(
      @Body User user,
      MyCallBack<User> callback
  );

  @DELETE("/delete")
  void delete(
      MyCallBack<User> callback
  );

  @HEAD("/head")
  void head(
      MyCallBack<User> callback
  );

  @FormUrlEncoded
  @POST("/post")
  void postFormUrlEncoded(
      @Field("param1") String param1,
      @Field("param2") String param2,
      MyCallBack<User> callback
  );

}
