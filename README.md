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
repositories {
    maven { url "https://oss.sonatype.org/content/repositories/snapshots/"}
}
dependencies {
    compile 'com.orhanobut:wasp:1.7-SNAPSHOT'
}
```

####Create a service interface

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

####And use it everywhere!

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

####For more details, check the website
http://orhanobut.github.io/wasp/


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
