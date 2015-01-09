#Wasp
Wasp is a wrapper for network libraries in order to make it simple. Currently volley is used as default current
network stack. The idea is to get rid of request classes and gather them into one place and easy to access with the
maximum readability and easiness. The idea is the same with retrofit (Thanks).

###Add dependency
```groovy
repositories {
    maven { url "https://oss.sonatype.org/content/repositories/snapshots/"}
}
dependencies {
    compile 'com.orhanobut:wasp:1.0.0-SNAPSHOT@aar'
}
```

###Usage
Create a service interface. This will be used to make the network requests.You can use appropriate annotations to define the request. 

- @GET,@DELETE,@POST,@PUT can be used for http method. 
- @Path is used for replacement for specific words in the url. 
- @Query is used to add query params
- @Header is used to add headers.

```java
public interface GitHubService {

    @GET("/repos/{user}/{repo}")
    void fetchRepo(@Path("user") String user,
                   @Path("repo") String repo,
                   RepoCallBack callBack
    );

    /**
    * query params will be added to url, ie : example.com/users/nr4bt/repos?page=1&sort=asc
    **/
    @GET("/users/{user}/repos")
    void fetchRepoBySearch(@Path("user") String user,
                           @Query("page") int pageNumber,
                           @Query("sort") String sort,
                           RepoSearchCallBack callBack
    );

    @POST("/repos/{user}/{repo}")
    void addName(@Path("user") String user,
                 @Header("auth") String authToken,
                 @Body Repo repo,
                 RepoCallBack callBack
    );
}
```

Add callback for the request response. Specify the response object type before using. Response object will be returned if the request is success. Gson is used as default parser.
```java
public class RepoCallBack implements CallBack<Repo> {

    @Override
    public void onSuccess(Repo repo) {
        //to do something
    }

    @Override
    public void onError(WaspError error) {
        //to do something
    }
}
```

Initialize the service. The best approach is the initialize this in the Application class and use it everywhere.
```java
GitHubService service = new Wasp.Builder(this)    
        .setEndpoint("https://api.github.com")   // Must be set
        .setLogLevel(LogLevel.ALL)               // Optional, default ALL   
        .setParser(new GsonParser())             // Optional, you can pass your gson object as parameter if needed
        .build()                                 // Must be called
        .create(MyService.class);                // Must be called in order to create object           
```

Make a network call by using service object. Request will be handled in the background and callback will be called.
```java
service.fetchRepo("nr4bt","wasp",new RepoCallBack());
```

####TODO
- Volley retry policy
- mock data support (local file or dynamic)
- certificate pinning support
- trust-all certificate support for testing purposes
- auto body creation for simple jsons to prevent model implementation
- Improve log
- Improve error handling and error response
- Improve the test coverage
