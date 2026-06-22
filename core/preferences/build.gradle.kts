plugins {
    id("flow.android.library")
}

android {
    namespace = "flow.securestorage"
}

dependencies {
    implementation(project(":core:dispatchers"))
    implementation(project(":core:models"))

    implementation(libs.androidx.security.ktx)
    // Coroutines were previously pulled in transitively via hilt-android.
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit4)
    testImplementation(libs.json)
}
