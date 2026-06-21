import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/**
 * Convention plugin for shared, non-UI Kotlin Multiplatform library modules.
 *
 * Targets: Android, JVM (desktop), iOS and macOS. Under AGP 9.0+ the Kotlin
 * Multiplatform plugin is no longer compatible with `com.android.library`, so the
 * dedicated `com.android.kotlin.multiplatform.library` plugin is used instead and
 * the Android library is configured inside `kotlin { androidLibrary { } }`.
 *
 * Apple targets are declared so common code is verified against them, but they are
 * only compiled on Apple hosts — the Android CI tasks never trigger their
 * compile/test tasks.
 *
 * The Android namespace is derived from the Gradle path, e.g. `:core:models` ->
 * `flow.core.models`. These modules carry no Android resources, so the namespace
 * only scopes the generated R/BuildConfig and just needs to be unique.
 */

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
    id("com.diffplug.spotless")
}

kotlin {
    jvmToolchain(17)

    androidLibrary {
        namespace = "flow." + path.removePrefix(":").replace(":", ".").replace("-", "_")
        compileSdk = 36
        minSdk = 23

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()

    applyDefaultHierarchyTemplate()
}

// Mirrors flow.conventions.StaticAnalysisConventionPlugin for KMP modules.
spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("${layout.buildDirectory}/**/*.kt")
        ktlint()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }
}
