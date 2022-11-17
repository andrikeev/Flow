plugins {
    id("flow.android.library")
    id("flow.android.hilt")
}

android {
    namespace = "flow.network"
}

dependencies {
    implementation(project(":core:auth"))
    implementation(project(":core:dispatchers"))
    implementation(project(":core:models"))
    implementation(project(":core:networkutils"))

    implementation(libs.jsoup)
    implementation(libs.bundles.network)
}
