plugins {
    id("flow.android.feature")
    id("flow.android.library.compose")
}

android {
    namespace = "flow.main"
}

dependencies {
    implementation(libs.androidx.activity.compose)
}
