plugins {
    id("flow.android.library")
    id("flow.android.library.compose")
    id("kotlin-parcelize")
}

android {
    namespace = "flow.ui"

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=com.google.accompanist.permissions.ExperimentalPermissionsApi",
        )
    }
}

dependencies {
    api(project(":core:designsystem"))

    implementation(project(":core:logger"))
    implementation(project(":core:models"))

    implementation(libs.accompanist.permissions)
    implementation(libs.bundles.coil)
}
