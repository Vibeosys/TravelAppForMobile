# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android SDK\SDK1/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-keep public class com.google.android.gms.**
-keep class com.android.volley.toolbox.**
-keep class android.net.http.AndroidHttpClient
-keepclasseswithmembernames class com.vibeosys.travelapp.** { *;}
-keep class com.google.gson.examples.android.model.** { *; }
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-dontwarn com.vibeosys.travelapp.**
-dontwarn com.android.volley.toolbox.**
-dontwarn com.google.android.gms.**
-dontwarn android.support.v7.**
