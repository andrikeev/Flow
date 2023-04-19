plugins {
    id("flow.kotlin.library")
    id("flow.kotlin.serialization")
}

dependencies {
    api(project(":core:network:api"))

    implementation(libs.bundles.ktor.client)
}
