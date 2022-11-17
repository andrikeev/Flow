plugins {
    id("flow.android.feature")
    id("flow.android.library.compose")
}

android {
    namespace = "flow.login"
}

dependencies {
    implementation(project(":core:auth"))
    implementation(project(":core:logger"))
}
