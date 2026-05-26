plugins {
    id("flow.android.library")
    id("flow.android.library.compose")
    id("flow.kotlin.serialization")
}

android {
    namespace = "flow.navigation"
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xcontext-parameters")
    }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:logger"))
    implementation(project(":core:ui"))

    api(libs.androidx.navigation3.runtime)
    api(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewModel.navigation3)
    implementation(libs.androidx.lifecycle.viewModel.savedState)
}
