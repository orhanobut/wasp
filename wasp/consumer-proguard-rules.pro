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