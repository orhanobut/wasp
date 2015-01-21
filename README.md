#Wasp
Wasp is compact, complete, easy-in-use and all-in-one network solution. 

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
    compile 'com.orhanobut:wasp:1.3-SNAPSHOT'
}
```

####Create a service interface.

```java
public interface GitHubService {

    @GET("/repos/{user}/{repo}")
    void fetchRepo(
           @Path("user") String user,
           @Path("repo") String repo,
           CallBack<Repo> callBack
    );

    @Mock
    @Headers 
    @POST("/repos/{user}")
    void addName(
          @Path("user") String user,
          @Header("auth") String authToken,
          @Body Repo repo,
          CallBack<Repo> callBack
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

####Load images easily

```java
Wasp.Image.from(url)
      .to(imageView)
      .setErrorResource(errorImage)     // Optional
      .setDefaultResource(defaulImage)  // Optional
      .fit()                            // Optional, in TODO
      .cropCenter()                     // Optional, in TODO
      .resize(100,200)                  // Optional, in TODO
      .load();
```

####For more details, check the website
http://orhanobut.github.io/wasp/

###TODO
Check wiki

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
