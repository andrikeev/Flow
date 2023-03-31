plugins {
    id("flow.android.library")
    id("flow.android.library.compose")
    id("kotlin-parcelize")
}

android {
    namespace = "flow.navigation"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:logger"))
    implementation(project(":core:ui"))

    implementation(libs.accompanist.navigation.animation)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.viewModel.savedState)
}
