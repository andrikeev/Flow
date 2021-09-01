plugins {
    id("com.android.application")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
    kotlin("android")
    kotlin("kapt")
}

val signingStorePassword: String? by project
val signingKeyAlias: String? by project
val signingKeyPassword: String? by project

android {
    compileSdk = libs.versions.androidSdk.compileSdk.get().toInt()
    buildToolsVersion = libs.versions.androidSdk.buildTools.get()

    defaultConfig {
        applicationId = "me.rutrackersearch.app"
        minSdk = libs.versions.androidSdk.minSdk.get().toInt()
        targetSdk = libs.versions.androidSdk.targetSdk.get().toInt()
        versionCode = 21
        versionName = "3.2.2"
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
        kotlinCompilerExtensionVersion = libs.versions.androidxCompose.get()
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.material3)
    implementation(libs.bundles.accompanist)
    implementation(libs.bundles.coil)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.hilt)
    implementation(libs.bundles.navigation)
    implementation(libs.bundles.room)
    implementation(libs.bundles.work)
    implementation(project(":domain"))
    implementation(project(":data"))

    debugImplementation(libs.bundles.composeDebug)

    kapt(libs.bundles.hiltCompiler)
    kapt(libs.bundles.roomCompile)
}
