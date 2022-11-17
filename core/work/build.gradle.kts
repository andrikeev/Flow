plugins {
    id("flow.android.library")
    id("flow.android.hilt")
}

android {
    namespace = "flow.work"
}

dependencies {
    implementation(project(":core:auth"))
    implementation(project(":core:common"))
    implementation(project(":core:database"))
    implementation(project(":core:models"))
    implementation(project(":core:network"))
    implementation(project(":core:notifications"))

    implementation(libs.bundles.work)

    kapt(libs.hilt.ext.compiler)
}
