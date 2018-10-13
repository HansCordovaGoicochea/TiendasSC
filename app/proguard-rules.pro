# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

 #skip every public class that extends com.orm.SugarRecord
# #and their public/protected members
# -keep public class * extends com.orm.SugarRecord {
#  public protected *;
#}
#-keep class com.orm.** { *; }
#
#-keep class com.scientechperu.pideloya.Clases.** { *; }
#
#
##Keep jsoup
#-keep public class org.jsoup.** {
#public *;
#}
#
## Prevent Proguard from inlining methods that are intentionally extracted to ensure locals have a
## constrained liveness scope by the GC. This is needed to avoid keeping previous request references
## alive for an indeterminate amount of time. See also https://github.com/google/volley/issues/114
#
#-keepclassmembers,allowshrinking,allowobfuscation class com.android.volley.NetworkDispatcher {
#    void processRequest();
#}
#-keepclassmembers,allowshrinking,allowobfuscation class com.android.volley.CacheDispatcher {
#    void processRequest();
#}
#
#
#-keepclassmembers class * extends android.os.AsyncTask {
#    protected void onPreExecute();
#    protected *** doInBackground(...);
#    protected void onPostExecute(...);
#}
#
#
#-keep class android.support.v7.app.AppCompatViewInflater{ <init>(...); }
#-dontwarn android.support.v7.**
#-keep class android.support.v7.** { *; }
#-keep interface android.support.v7.** { *; }
#
#-keep class android.support.v7.widget.SearchView { *; }
#
#-keep class cn.pedant.SweetAlert.Rotate3dAnimation {
#  public <init>(...);
#}