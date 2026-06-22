plugins {
    id("flow.android.library")
}

android {
    namespace = "flow.auth"
}

dependencies {
    api(project(":core:auth:api"))

    implementation(project(":core:common"))
    implementation(project(":core:dispatchers"))
    implementation(project(":core:models"))
    implementation(project(":core:network:api"))
    implementation(project(":core:preferences"))

    // Coroutines were previously pulled in transitively via hilt-android.
    implementation(libs.kotlinx.coroutines.core)
    // Target DI for the KMP graph. On Android the binding is bridged into Hilt (see :app).
    implementation(libs.koin)
}
