@file:Suppress("UnstableApiUsage")

plugins {
    id("flow.android.application")
    id("flow.android.hilt")
    id("flow.kotlin.serialization")
}

android {
    namespace = "me.rutrackersearch.app"

    defaultConfig {
        applicationId = "me.rutrackersearch.app"
        versionCode = 46
        versionName = "4.7.0"
    }

    buildFeatures {
        buildConfig = true
    }

    val releaseSigningConfig = signingConfigs.create("release") {
        val storeFilePath = System.getenv("KEYSTORE_FILE")
            ?: project.findProperty("KEYSTORE_FILE") as String?
        if (!storeFilePath.isNullOrBlank() && file(storeFilePath).exists()) {
            storeFile = file(storeFilePath)
            storePassword = System.getenv("KEYSTORE_PASSWORD")
                ?: project.findProperty("KEYSTORE_PASSWORD") as String?
            keyAlias = System.getenv("KEY_ALIAS")
                ?: project.findProperty("KEY_ALIAS") as String?
            keyPassword = System.getenv("KEY_PASSWORD")
                ?: project.findProperty("KEY_PASSWORD") as String?
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = releaseSigningConfig.storeFile
                ?.let { releaseSigningConfig }
                ?: signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = ".dev"
        }
    }
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
