plugins {
    id("com.android.application")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = libs.versions.androidSdk.compileSdk.get().toInt()
    buildToolsVersion = libs.versions.androidSdk.buildTools.get()

    defaultConfig {
        applicationId = "me.rutrackersearch.app"
        minSdk = libs.versions.androidSdk.minSdk.get().toInt()
        targetSdk = libs.versions.androidSdk.targetSdk.get().toInt()
        versionCode = 22
        versionName = "4.0.0"
        vectorDrawables {
            useSupportLibrary = true
        }
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

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = libs.versions.java.get()
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.security.ktx)
    implementation(libs.jsoup)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.material3)
    implementation(libs.okhttp.logging)

    implementation(libs.bundles.accompanist)
    implementation(libs.bundles.coil)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.hilt)
    implementation(libs.bundles.navigation)
    implementation(libs.bundles.network)
    implementation(libs.bundles.room)
    implementation(libs.bundles.work)

    debugImplementation(libs.bundles.composeDebug)

    kapt(libs.bundles.hiltCompiler)
    kapt(libs.bundles.roomCompile)
}
