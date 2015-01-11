#Wasp
Wasp is a wrapper for network libraries in order to make it simple.
Currently volley is used as default current network stack.
The idea is to get rid of request classes and gather them into one place and easy to access them.
It is customizable for every situation and you don't need to struggle with json converter as well.
We try to implement the best practices and tools in order to make everything compact.
You don't need to worry about to combine everything since we handled everything already.
Following tools are used in the system:

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
* Volley retry policy
* Mock data support (local file or dynamic)
* Certificate pinning support
* Trust-all certificate support for testing purposes
* Optimize imageloader and make it easy in use.
* Improve log
* Improve error handling and error response
* Improve the test coverage
