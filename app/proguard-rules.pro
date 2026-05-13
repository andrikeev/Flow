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

# kotlinx.serialization: keep generated serializers and Companion-held $serializer fields
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclasseswithmembers class **.*$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}
-if class **.*$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers class <1>.<2> {
    <1>.<2>$Companion Companion;
}
-keepclasseswithmembers class * {
    public static **$Companion Companion;
}

# Hilt / Dagger
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel
-keep,allowobfuscation @interface dagger.hilt.**
-keep,allowobfuscation @interface javax.inject.**

# Ktor client/server use reflection for engines and plugins
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { *; }
-dontwarn io.ktor.**
-dontwarn io.netty.**
-dontwarn org.slf4j.**

# OkHttp / Okio (transitive via Ktor + Coil)
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**

# Coroutines: drop service-loader warnings on Android
-dontwarn kotlinx.coroutines.debug.**

# Orbit MVI uses reflection on container/intent classes
-keep class org.orbitmvi.orbit.** { *; }

# Jsoup occasionally referenced reflectively by parsers
-keep class org.jsoup.** { *; }
-dontwarn org.jsoup.**

# Room: generated _Impl classes are accessed by reflection at runtime
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

-keepattributes SourceFile,LineNumberTable,RuntimeVisibleAnnotations,AnnotationDefault
-renamesourcefileattribute SourceFile
