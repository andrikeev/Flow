plugins {
    id("flow.android.library")
    id("flow.android.hilt")
}

android {
    namespace = "flow.auth"
}

dependencies {
    implementation(project(":core:dispatchers"))
    implementation(project(":core:logger"))
    implementation(project(":core:models"))
    implementation(project(":core:networkutils"))
    implementation(project(":core:securestorage"))

    implementation(libs.jsoup)
    implementation(libs.bundles.network)
}
