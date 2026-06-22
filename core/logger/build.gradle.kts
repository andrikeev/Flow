plugins {
    id("flow.android.library")
}

android {
    namespace = "flow.logger"
}

dependencies {
    // Target DI for the KMP graph. On Android the binding is bridged into Hilt (see :app).
    implementation(libs.koin)
}
