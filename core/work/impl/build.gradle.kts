plugins {
    id("flow.android.library")
}

android {
    namespace = "flow.work"
}

dependencies {
    api(project(":core:work:api"))

    implementation(project(":core:domain"))
    implementation(project(":core:models"))
    implementation(project(":core:notifications"))

    implementation(libs.androidx.work)
    implementation(libs.koin.androidx.workmanager)
    implementation(libs.kotlinx.coroutines.core)
}
