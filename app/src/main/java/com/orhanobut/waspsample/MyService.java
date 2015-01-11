package com.orhanobut.waspsample;

import com.orhanobut.wasp.CallBack;
import com.orhanobut.wasp.http.Body;
import com.orhanobut.wasp.http.BodyMap;
import com.orhanobut.wasp.http.DELETE;
import com.orhanobut.wasp.http.GET;
import com.orhanobut.wasp.http.POST;
import com.orhanobut.wasp.http.PUT;
import com.orhanobut.wasp.http.RetryPolicy;

import java.util.Map;

/**
 * @author Orhan Obut
 */
public interface MyService {

    @GET("/ip")
    void fetchIp(
            CallBack<Ip> callBack
    );

    @POST("/post")
    void postFoo(
            @Body Ip ip,
            CallBack<Ip> callBack
    );

    @PUT("/put")
    void putFoo(
            @Body Ip ip,
            CallBack<Ip> callBack
    );

    @DELETE("/delete")
    void deleteFoo(
            CallBack<Ip> callBack
    );

    @RetryPolicy(initialTimeout = 1)
    @PUT("/put")
    void putFooMap(
            @BodyMap Map bodyMap,
            CallBack<Ip> callBack
    );

}