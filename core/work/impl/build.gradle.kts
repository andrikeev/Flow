plugins {
    id("flow.android.library")
    id("flow.android.hilt")
}

android {
    namespace = "flow.work"
}

dependencies {
    api(project(":core:work:api"))

    implementation(project(":core:domain"))
    implementation(project(":core:models"))
    implementation(project(":core:notifications"))

    implementation(libs.bundles.work)
}
