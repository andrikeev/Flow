plugins {
    id("flow.android.feature")
    id("flow.android.library.compose")
}

android {
    namespace = "flow.topic"
}

dependencies {
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.paging.compose)
}
