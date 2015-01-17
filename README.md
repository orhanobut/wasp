#Wasp
Wasp is compact, complete, easy-in-use and simple network solution. 

Wasp uses:
- Volley
- Gson
- OkHttp

Wasp provides:
- Easy implementation
- Mocking network calls
- Request Interceptor to add attributes (query params, headers, retry policy) to each call
- Call based headers
- Call based endpoint url
- Call based retry policy
- Cookie management
- Certicate management
- Image loading in an easy way

###Add dependency
```groovy
repositories {
    maven { url "https://oss.sonatype.org/content/repositories/snapshots/"}
}
dependencies {
    compile 'com.orhanobut:wasp:1.0.0-SNAPSHOT@aar'
}
```

####Create a service interface.

```java
public interface GitHubService {

    @GET("/repos/{user}/{repo}")
    void fetchRepo(@Path("user") String user,
                   @Path("repo") String repo,
                   RepoCallBack callBack
    );

    @Mock
    @Headers 
    @POST("/repos/{user}/{repo}")
    void addName(@Path("user") String user,
                 @Header("auth") String authToken,
                 @Body Repo repo,
                 RepoCallBack callBack
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
    .build()
    .create(MyService.class);
```

####And use it everywhere

```java
service.fetchRepo("github","wasp", new CallBack<List<Repo>>{
    
    @Override
    public void onSuccess(List<Repo> repos) {
        // do something
    }
    
    @Override
    public void onError(WaspError error) {
        // handle error
    }
});
```

####For more details, check the website
http://orhanobut.github.io/wasp/

###TODO
Check wiki
