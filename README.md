[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Wasp-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1412)    [![API](https://img.shields.io/badge/API-10%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=10) [![Join the chat at https://gitter.im/orhanobut/wasp](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/orhanobut/wasp?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) [![](https://img.shields.io/badge/AndroidWeekly-%23143-blue.svg)](http://androidweekly.net/issues/issue-143)

#Wasp
Wasp is a compact and easy to use, 'all-in-one' network solution. 

<img src='https://github.com/orhanobut/wasp/blob/master/images/wasp-diagram.png'/>

Wasp internally uses:
- Volley for the network stack
- Gson for parsing
- OkHttp for the http stack

Wasp provides:
- Easy implementation
- **MOCK** response via text file or **auto generated** from model class!
- Request Interceptors to add attributes (query params, headers, retry policy) to each call
- **Call based headers**
- Api based end point
- Api based retry policy
- **Cookie management**
- **Certificate management**
- Painless Image loading

Wasp aims :
- There are many open issues to contribute. Get this chance to contribute and improve your knowledge!
- We want to make something that is useful and also motivates people to contribute

###Add dependency
```groovy
compile 'com.orhanobut:wasp:1.11'
```

####Create a service interface

```java
public interface GitHubService {

    @GET("/repos/{user}/{repo}")
    void getRepo(
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
#### Check wiki for more details

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
