plugins {
    id("flow.android.library")
    id("flow.android.library.compose")
}

android {
    namespace = "flow.designsystem"
}

dependencies {
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.compose.ui.util)
    api(libs.androidx.compose.runtime)

    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.material3)

    debugApi(libs.androidx.compose.ui.tooling)
}
