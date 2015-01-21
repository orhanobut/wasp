package com.orhanobut.waspsample;

import com.orhanobut.wasp.CallBack;
import com.orhanobut.wasp.http.Auth;
import com.orhanobut.wasp.http.Body;
import com.orhanobut.wasp.http.BodyMap;
import com.orhanobut.wasp.http.DELETE;
import com.orhanobut.wasp.http.EndPoint;
import com.orhanobut.wasp.http.GET;
import com.orhanobut.wasp.http.Header;
import com.orhanobut.wasp.http.Mock;
import com.orhanobut.wasp.http.POST;
import com.orhanobut.wasp.http.PUT;

import java.util.List;
import java.util.Map;

/**
 * @author Orhan Obut
 */
public interface MyService {

    @Auth
    @GET("/ip")
    void fetchIps(
            CallBack<List<Ip>> callBack
    );

    @GET("/ip")
    void fetchIp(
            @Header("ParamHeaderKey") String paramHeader,
            @Body Ip ip,
            CallBack<Ip> callBack
    );

    @POST("/post")
    void postFoo(
            @Body Ip ip,
            CallBack<Ip> callBack
    );

    @Mock(statusCode = 300)
    @PUT("/put")
    void putFoo(
            @Body Ip ip,
            CallBack<Ip> callBack
    );

    @EndPoint("http://api.github.com")
    @DELETE("/delete")
    void deleteFoo(
            CallBack<Ip> callBack
    );

    //  @RetryPolicy(timeout = 1)
    @PUT("/put")
    void putFooMap(
            @BodyMap Map bodyMap,
            CallBack<Ip> callBack
    );

}
