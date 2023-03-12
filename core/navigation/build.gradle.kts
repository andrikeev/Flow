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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.viewModel.savedState)
    implementation(libs.androidx.navigation.compose)
}
