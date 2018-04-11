[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Wasp-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1412)    [![API](https://img.shields.io/badge/API-10%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=10) [![Join the chat at https://gitter.im/orhanobut/wasp](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/orhanobut/wasp?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) [![](https://img.shields.io/badge/AndroidWeekly-%23143-blue.svg)](http://androidweekly.net/issues/issue-143)

<img align='right' src='https://github.com/orhanobut/wasp/blob/master/images/logo_wasp.png' width='128' height='128'/>

# Deprecated
Unfortunately due to many reasons including maintenance cost, this library is deprecated. I recommend to use Retrofit/OkHttp instead. Currently this project only aims to provide some experimental functionalities.

# Wasp
A compact and easy to use, 'all-in-one' network solution. 

<img src='https://github.com/orhanobut/wasp/blob/master/images/wasp-diagram.png'/>

#### The problem
When it comes to daily development, you need more than just a library to handle networking, you need to handle mocking calls, using multiple end points, handling certificates and cookies and many other boiler plate code. With wasp, you can easily handle everything.

Wasp internally uses:
- Volley for the network stack
- Gson for parsing
- OkHttp for the http stack

Wasp provides:
- Easy implementation
- **MOCK** response via text file or **auto generated** from model class!
- Request Interceptors to add attributes (query params, headers, retry policy) to each call
- **Api call based headers**
- Api call based end point
- Api call based retry policy
- **Cookie management**
- **Certificate management**
- Painless Image loading
- **RxJava support**
- **Request cancelation**
- Sync request call
- Async request call

Wasp aims :
- There are many open issues to contribute. Get this chance to contribute and improve your knowledge!
- We want to make something that is useful and also motivates people to contribute

### Add dependency
More info https://jitpack.io/#orhanobut/wasp/1.15
```groovy
repositories {
  // ...
  maven { url "https://jitpack.io" }
}

dependencies {
  compile 'com.github.orhanobut:wasp:1.15'
}
```

#### Create a service interface

```java
public interface GitHubService {

  // Async call
  @GET("/repos/{id}")
  void getRepo(@Path("id") String id, Callback<Repo> callback);
  
  // Async call with WaspRequest (cancelable)
  @GET("/repos/{id}")
  WaspRequest getRepo(@Path("id") String id, Callback<Repo> callback);
    
  // Rx
  @Mock
  @POST("/repos")
  Observable<Repo> createRepo(@Body Repo repo);
  
  // sync call
  @GET("/users/{id}")
  User getUser(@Path("id") String id);
}
```

#### Initialize the wasp

```java
GitHubService service = new Wasp.Builder(this)
  .setEndpoint("https://api.github.com")
  .setRequestInterceptor                     // Optional
  .trustCertificates                         // Optional
  .setHttpStack                              // Optional
  .enableCookies                             // Optional
  .setNetworkMode(NetworkMode.MOCK)          // Optional(Used for Mock)
  .build()
  .create(GitHubService.class);
```

#### And use it everywhere!
Async
```java
service.getRepo(id, new Callback<Repo>{

  @Override
  public void onSuccess(Response response, Repo repo) {
    // do something
  }
  
  @Override
  public void onError(WaspError error) {
    // handle error
  }
});
```

Async with WaspRequest (cancelable)
```java
WaspRequest request = service.getRepo(id, new Callback<Repo>{

  @Override
  public void onSuccess(Response response, Repo repo) {
    // do something
  }
  
  @Override
  public void onError(WaspError error) {
    // handle error
  }
});
request.cancel();  //cancels the request
```

Rx
```java
Observable<Repo> observable = service.createRepo(repo);
```

Sync
```java
User user = service.getUser(id);
```
#### Check wiki for more details

### License
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
