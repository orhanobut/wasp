[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Wasp-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1412)    [![API](https://img.shields.io/badge/API-10%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=10) [![Join the chat at https://gitter.im/orhanobut/wasp](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/orhanobut/wasp?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) [![](https://img.shields.io/badge/AndroidWeekly-%23143-blue.svg)](http://androidweekly.net/issues/issue-143)

#Wasp
Wasp is a compact and easy to use, 'all-in-one' network solution. Wasp uses the interface approach of retrofit with **Volley** network stack and adds functionalities such as **MOCK**, **Certificate management**, **Cookie management** and more. Basically it provides a universal solution for all your networking needs.

<img src='https://github.com/orhanobut/wasp/blob/master/images/logo_wasp.png' width='128' height='128'/>

Wasp internally uses:
- Volley for the network stack
- Gson for parsing
- OkHttp for the http stack

Wasp provides:
- Easy implementation
- **MOCK** response via text file or **auto generated** from model class!
- Request Interceptors to add attributes (query params, headers, retry policy) to each call
- **Call based headers**
- Call based endpoint url
- Call based retry policy
- **Cookie management**
- **Certificate management**
- Painless Image loading

Wasp aims :
- There are many open issues to contribute. Get this chance to contribute and improve your knowledge!
- We want to make something that is useful and also motivates people to contribute

###Add dependency
```groovy
compile 'com.orhanobut:wasp:1.10'
```

####Create a service interface

```java
public interface GitHubService {

    @GET("/repos/{user}/{repo}")
    void fetchRepo(
           @Path("user") String user,
           @Path("repo") String repo,
           Callback<Repo> callback
    );

    @Mock
    @Headers 
    @POST("/repos/{user}")
    void addName(
          @Path("user") String user,
          @Header("auth") String authToken,
          @Body Repo repo,
          Callback<Repo> callback
    );
}
```

####Initialize the wasp

```java
GitHubService service = new Wasp.Builder(this)
    .setEndpoint("https://api.github.com")
    .setRequestInterceptor                     // Optional
    .trustCertificates                         // Optional
    .setHttpStack                              // Optional
    .enableCookies                             // Optional
    .setNetworkMode(NetworkMode.MOCK)          // Optional(Used for Mock)
    .build()
    .create(MyService.class);
```

####And use it everywhere!

```java
service.fetchRepo("github","wasp", new Callback<List<Repo>>{
    
    @Override
    public void onSuccess(WaspResponse response, List<Repo> repos) {
        // do something
    }
    
    @Override
    public void onError(WaspError error) {
        // handle error
    }
});
```

#### Add Body
@Body can be used to add an object for request body. Object will be converted to json.

```java
    @POST("/repos")
    void addName(
        @Body Repo repo,
        Callback<Repo> callback
    );

    service.addName(new Repo("3423",3),callback);
```

@BodyMap can be used to add a Map object instead of creating body class. It will be converted to json. You can use @BodyMap for the simple operations which you don't want to create a class.

```java
    @POST("/repos")
    void addName(
        @BodyMap Map map,
        Callback<Repo> callback
    );

    Map map = new HashMap<>();
    map.put("ip","3423");
    map.put("page",3);

    service.addName(map, callback);
```

####Add Query Params
@Query is used to add query params

```java
    @GET("/users/repos")
    void fetchRepoBySearch(
          @Query("page") int pageNumber,
          @Query("sort") String sort,
          Callback<Repo> callback
    );

    service.fetchRepoBySearch(2,"asc", callback);
    //output url is ENDPOINT/users/repos?page=2&sort=asc
```
@QueryMap is used to add query params using a map
```java
    @GET("/users/repos")
    void fetchRepoBySearch(
          @QueryMap Map queryParamsMap,
          Callback<Repo> callback
    );

    Map<String,String> map = new HashMap<>();
    map.put("sort","asc");
    map.put("offset", "100");

    service.fetchRepoBySearch(map, callback);
```

####Form-url-encoded
Use @Field annotation to provide key-value pairs

```java
    @FormUrlEncoded
    @POST("/users/repos")
    void fetchRepoBySearch(
          @Field("page") int pageNumber,
          @Field("sort") String sort,
          Callback<Repo> callback
    );

    service.fetchRepoBySearch(2,"asc", callback);
    //output url is ENDPOINT/users/repos?page=2&sort=asc
```
@FieldMap is used to add fields by map
```java
    @FormUrlEncoded
    @POST("/users/repos")
    void fetchRepoBySearch(
          @FieldMap Map queryParamsMap,
          Callback<Repo> callback
    );

    Map<String,String> map = new HashMap<>();
    map.put("sort","asc");
    map.put("offset", "100");

    service.fetchRepoBySearch(map, callback);
```

####Add Headers
@Header is used to add headers by using params

```java
    @GET("/repos")
    void fetchRepos(
          @Header("auth") String authToken,
          RepoCallBack<List<Repo> callBack
    );
```

@Headers is used to add static headers by adding to method

```java
    //Single static header
    @Headers("Accept-Language:en-En")
    @GET("/users")
    void fetchUsers(
          Callback<List<User> callback
    );

    // Multiple static headers
    @Headers({
        "Accept-Language:en-En",
        "Content-type:application/json"
    })
```

##### Request Interceptor
You can intercept each request and add some additional information. You can either implement RequestInterceptor interface or use the SimpleInterceptor class. Use SimpleInterceptor if you don't need to implement each feature.

Add headers to each request
```java
  RequestInterceptor interceptor = new SimpleInterceptor() {
      @Override                                                
      public void onHeadersAdded(Map headers) {
          super.onHeadersAdded(headers);                       
          headers.put("key","value");                          
      }                                                        
  }
```
Add additional query parameters to the each request
```java
  RequestInterceptor interceptor = new SimpleInterceptor() {
      @Override
      public void onQueryParamsAdded(Map params) {
          super.onQueryParamsAdded(params);
          params.put("name","something");
      }                                                       
  }
```
Add retry policy to the each request

```java
  RequestInterceptor interceptor = new SimpleInterceptor() {
      @Override
      public WaspRetryPolicy getRetryPolicy() {
          return new WaspRetryPolicy(45000, 3, 1.5f);
      }                                                      
  }
```

Add auth token to the each request or filtered requests. Return a new AuthToken object which accepts authtoken value and filter enabled. If you enabled the filter, all request which has @Auth annotation will use the auth token in the header. If you disabled filter, each request will add the token.

```java
  RequestInterceptor interceptor = new SimpleInterceptor() {
      @Override
      public AuthToken getAuthToken() {
          return new AuthToken(token, true);
      }                                                     
  }

    @Auth
    @GET("/users")
    void fetchUsers(
          Callback<List<User> callback
    );
```

And finally set it to the builder
```java
  GitHubService service = new Wasp.Builder(this)    
        .setEndpoint("https://api.github.com")   
        .setRequestInterceptor(interceptor)        
        .build()                        
        .create(MyService.class);   
```

#### Retry Policy
You can set retry policy for each call by using request interceptor

```java
  RequestInterceptor interceptor = new SimpleInterceptor() {
      @Override
      public WaspRetryPolicy getRetryPolicy() {
          return new WaspRetryPolicy(45000, 3, 1.5f);
      }                                                      
  }
```

You can use annotation to add specific policy for the specific calls. Annotation always override the request interceptor if both are used at the same time

```java
    @RetryPolicy(timeout = 10000)
    @GET("/users")
    void fetchUsers(
          Callback<List<User>> callback
    );
```

#### Http Stack
You can set your custom http stack instead of default. Default is OkHttp.

```java
  GitHubService service = new Wasp.Builder(this)    
        .setEndpoint("https://api.github.com")   
        .setHttpStack(new YourHttpStack());       
        .build()                        
        .create(MyService.class);   
```

##### Add different end points for different network calls
You can add different end point url for some network calls, it will override the base url.

```java
    @EndPoint("http://www.google.com")
    @GET("/users")
    void fetchUsers(
          Callback<List<User>> callback
    );
```

#### Mocking
You can mock your network calls easily by using mock annotation.
@Mock Uses auto generate feature mock regarding to your response type

```java
    @Mock
    @GET("/user")
    void fetchUser(
          Callback<User> callback
    );
```
@Mock(path="users.json") : Uses local file to generate mock. Local files must be under assets folder. This will return a response with the generated content by given path, with the status code 200
```java
    @Mock(path="user.json")
    @GET("/user")
    void fetchUser(
          Callback<User> callback
    );
```
@Mock(statusCode=404) : Returns a fail response with status code 404
```java
    @Mock(statusCode=404)
    @GET("/user")
    void fetchUser(
          Callback<User> callback
    );
```
@Mock(statusCode=201) : Returns a success with the status code 201 and auto generated response
```java
    @Mock(statusCode=201)
    @GET("/user")
    void fetchUser(
         Callback<User> callback
    );
```

#### Cookie Management
You can easily handle cookies in two ways:
- Set a CookiePolicy and let the CookieManager to use the default CookieStore implementation

```java
  GitHubService service = new Wasp.Builder(this)    
        .setEndpoint("https://api.github.com")   
        .enableCookies(CookiePolicy.ACCEPT_ALL)     
        .build()                        
        .create(MyService.class);
```

Provide also your own implementation of CookieStore which will be used by CookieManager

```java
  GitHubService service = new Wasp.Builder(this)    
        .setEndpoint("https://api.github.com")   
        .enableCookies(new YourCookieStore(), CookiePolicy.ACCEPT_ALL)   
        .build()                        
        .create(MyService.class);
```

#### Certificate Management
You can make use of this feature in two ways:
Trust All Certificates: Most of the time test servers do not use a certificate which is signed by a CA. Therefore, connections to those servers fail at SSL Handshake step. To solve this, you can let Wasp to accept all certificates (Note that, this should only be used for testing purposes because it makes the connections vulnerable to security attacks.)

```java
  GitHubService service = new Wasp.Builder(this)    
        .setEndpoint("https://api.github.com")   
        .trustCertificates()  //Trust All Certificates
        .build()                        
        .create(MyService.class);
```

Certificate Pinning: Create a BKS file of your server certificate and put it under res/raw folder. Than, let Wasp use your certificate for SSL Handshake with the server by providing your raw resource id and keystore pasword.

```java
  GitHubService service = new Wasp.Builder(this)    
        .setEndpoint("https://api.github.com")   
        .trustCertificates(R.raw.YOUR_TRUST_STORE, "YOUR_PASSWORD") //Trust only to the given certificates
        .build()                        
        .create(MyService.class);
```

#### Image handling
With wasp, you can also download and display the images. Wasp provides a good solution for flickering as well.
```java
    Wasp.Image
        .from("url")
        .setDefault(R.id.image)
        .setError(R.id.image)
        .to(imageView)
        .load();
```


####ProGuard

If you are using ProGuard you should add the following options to your configuration file:
**Note:** Other than these options you may also need to keep your network related model classes.

```
#Wasp
-keepattributes *Annotation*
-keep class com.orhanobut.wasp.** { *; }
-keepclassmembernames interface * {
    @com.orhanobut.wasp.http.* <methods>;
}

#Gson
-keep class com.google.gson.** { *; }
-keepattributes Signature

#OkHttp
-dontwarn com.squareup.okhttp.**
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
```


<img src='https://github.com/orhanobut/wasp/blob/master/images/wasp-diagram.png'/>

#### You might also like
- [Hawk](https://github.com/orhanobut/hawk) Secure simple key-value storage
- [Bee](https://github.com/orhanobut/bee) QA/Debug tool
- [DialogPlus](https://github.com/orhanobut/dialogplus) Easy, simple dialog solution
- [SimpleListView](https://github.com/orhanobut/simplelistview) Simple basic listview implementation with linearlayout

###License
<pre>
Copyright 2014 Orhan Obut

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>
