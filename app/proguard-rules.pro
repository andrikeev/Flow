# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
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

-dontwarn com.google.errorprone.annotations.Immutable
-dontwarn org.bouncycastle.jsse.*
-dontwarn org.bouncycastle.jsse.provider.*
-dontwarn org.conscrypt.*
-dontwarn org.openjsse.javax.net.ssl.*
-dontwarn org.openjsse.net.ssl.*
-dontwarn org.slf4j.impl.StaticLoggerBinder

-keep class com.google.crypto.tink.** { *; }
-keep class flow.network.dto.** { *; }

-keepattributes SourceFile,LineNumberTable,RuntimeVisibleAnnotations,AnnotationDefault
-renamesourcefileattribute SourceFile
