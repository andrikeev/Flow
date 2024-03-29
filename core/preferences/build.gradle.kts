plugins {
    id("flow.android.library")
    id("flow.android.hilt")
}

android {
    namespace = "flow.securestorage"
}

dependencies {
    implementation(project(":core:dispatchers"))
    implementation(project(":core:models"))

    implementation(libs.androidx.security.ktx)

    testImplementation(libs.junit4)
    testImplementation(libs.json)
}
