@file:Suppress("UnstableApiUsage")

plugins {
    id("flow.android.application")
    id("flow.android.hilt")
}

android {
    defaultConfig {
        applicationId = "me.rutrackersearch.app"
        versionCode = 24
        versionName = "4.1.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    namespace = "me.rutrackersearch.app"
}

dependencies {
    implementation(project(":core:auth"))
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:dispatchers"))
    implementation(project(":core:domain"))
    implementation(project(":core:downloads"))
    implementation(project(":core:logger"))
    implementation(project(":core:models"))
    implementation(project(":core:network"))
    implementation(project(":core:notifications"))
    implementation(project(":core:securestorage"))
    implementation(project(":core:ui"))
    implementation(project(":core:work"))

    implementation(project(":feature:account"))
    implementation(project(":feature:forum"))
    implementation(project(":feature:login"))
    implementation(project(":feature:menu"))
    implementation(project(":feature:search"))
    implementation(project(":feature:topic"))
    implementation(project(":feature:topics"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.material3)

    implementation(libs.bundles.accompanist)
    implementation(libs.bundles.coil)
    implementation(libs.bundles.navigation)
    implementation(libs.bundles.network)
    implementation(libs.bundles.orbit)
}
