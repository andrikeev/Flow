plugins {
    id("flow.android.library")
    id("flow.android.library.compose")
    id("flow.android.hilt")
}

android {
    namespace = "flow.testing"
}

dependencies {
    api(project(":core:auth:api"))
    api(project(":core:auth:impl"))
    api(project(":core:data"))
    api(project(":core:dispatchers"))
    api(project(":core:downloads"))
    api(project(":core:logger"))
    api(project(":core:models"))
    api(project(":core:work:impl"))

    api(libs.junit4)
    api(libs.androidx.test.core)
    api(libs.kotlinx.coroutines.test)

    api(libs.androidx.test.runner)
    api(libs.androidx.test.rules)
    api(libs.androidx.compose.ui.test)
    api(libs.hilt.android.testing)
    api(libs.mockk.android)

    debugApi(libs.androidx.compose.ui.testManifest)
}
