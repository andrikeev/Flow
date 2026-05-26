plugins {
    id("flow.android.library")
    id("flow.android.library.compose")
    id("kotlin-parcelize")
}

android {
    namespace = "flow.ui"
}

dependencies {
    api(project(":core:designsystem"))

    implementation(project(":core:logger"))
    implementation(project(":core:models"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.bundles.coil)
    implementation(libs.material3)
}
