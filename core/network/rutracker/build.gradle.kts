plugins {
    id("flow.kotlin.library")
}

dependencies {
    api(project(":core:network:api"))

    implementation(libs.ktor.client.core)
    implementation(libs.ksoup)

    testImplementation(libs.junit4)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.ktor.client.mock)
}
