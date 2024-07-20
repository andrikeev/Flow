plugins {
    id("flow.android.library")
    id("flow.android.library.compose")
    id("kotlin-parcelize")
}

android {
    namespace = "flow.ui"
}

kotlin {
    compilerOptions {
        optIn.addAll(
            "com.google.accompanist.permissions.ExperimentalPermissionsApi",
        )
    }
}

dependencies {
    api(project(":core:designsystem"))

    implementation(project(":core:logger"))
    implementation(project(":core:models"))

    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.bundles.coil)
    implementation(libs.material3)
}
