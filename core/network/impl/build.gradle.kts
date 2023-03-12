plugins {
    id("flow.android.library")
    id("flow.android.hilt")
}

android {
    namespace = "flow.network"
}

dependencies {
    api(project(":core:network:api"))

    implementation(project(":core:dispatchers"))
    implementation(project(":core:models"))
    implementation(project(":core:network:proxy"))
    implementation(project(":core:network:rutracker"))

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.logging)
}
