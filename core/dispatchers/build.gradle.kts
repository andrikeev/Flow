plugins {
    id("flow.android.library")
}

android {
    namespace = "flow.dispatchers"
}

dependencies {
    // Coroutines were previously pulled in transitively via hilt-android; now declared explicitly.
    implementation(libs.kotlinx.coroutines.core)
    // Target DI for the KMP graph. On Android the binding is bridged into Hilt (see :app).
    implementation(libs.koin)
}
