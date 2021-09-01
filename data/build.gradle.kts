plugins {
    id("com.android.library")
    id("dagger.hilt.android.plugin")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = libs.versions.androidSdk.compileSdk.get().toInt()
    buildToolsVersion = libs.versions.androidSdk.buildTools.get()
    defaultConfig {
        minSdk = libs.versions.androidSdk.minSdk.get().toInt()
        targetSdk = libs.versions.androidSdk.targetSdk.get().toInt()
    }
}

dependencies {
    api(libs.bundles.hilt)
    api(libs.bundles.work)
    api(libs.javax.inject)
    api(libs.kotlinx.coroutines.core)
    api(project(":domain"))

    implementation(libs.bundles.network)
    implementation(libs.bundles.room)
    implementation(libs.androidx.security.ktx)

    kapt(libs.bundles.hiltCompiler)
    kapt(libs.bundles.roomCompile)
}
