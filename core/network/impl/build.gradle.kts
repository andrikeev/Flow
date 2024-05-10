plugins {
    id("flow.android.library")
    id("flow.android.hilt")
    id("flow.kotlin.serialization")
}

android {
    namespace = "flow.network"

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlin.io.encoding.ExperimentalEncodingApi",
        )
    }
}

dependencies {
    api(project(":core:network:api"))

    implementation(project(":core:auth:api"))
    implementation(project(":core:data"))
    implementation(project(":core:dispatchers"))
    implementation(project(":core:logger"))
    implementation(project(":core:models"))
    implementation(project(":core:network:rutracker"))

    implementation(libs.coil.kt)

    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.serialization.kotlinx.json)

    debugImplementation(libs.chucker)
}
