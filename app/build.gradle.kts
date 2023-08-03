@file:Suppress("UnstableApiUsage")

import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension

plugins {
    id("flow.android.application")
    id("flow.android.hilt")
}

android {
    defaultConfig {
        applicationId = "me.rutrackersearch.app"
        versionCode = 38
        versionName = "4.5.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro",
            )
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = true
            }
        }
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = ".dev"
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = false
            }
        }
    }

    namespace = "me.rutrackersearch.app"
}

dependencies {
    implementation(project(":core:auth:impl"))
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:dispatchers"))
    implementation(project(":core:domain"))
    implementation(project(":core:downloads"))
    implementation(project(":core:logger"))
    implementation(project(":core:models"))
    implementation(project(":core:navigation"))
    implementation(project(":core:network:impl"))
    implementation(project(":core:notifications"))
    implementation(project(":core:preferences"))
    implementation(project(":core:ui"))
    implementation(project(":core:work:impl"))

    implementation(project(":feature:account"))
    implementation(project(":feature:bookmarks"))
    implementation(project(":feature:category"))
    implementation(project(":feature:connection"))
    implementation(project(":feature:favorites"))
    implementation(project(":feature:forum"))
    implementation(project(":feature:login"))
    implementation(project(":feature:main"))
    implementation(project(":feature:menu"))
    implementation(project(":feature:rating"))
    implementation(project(":feature:search"))
    implementation(project(":feature:search_input"))
    implementation(project(":feature:search_result"))
    implementation(project(":feature:topic"))
    implementation(project(":feature:visited"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.bundles.orbit)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)

    debugImplementation(libs.leakcanary)
}
