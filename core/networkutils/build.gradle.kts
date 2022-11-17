plugins {
    id("flow.kotlin.library")
}

dependencies {
    implementation(libs.jsoup)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.okhttp.client)
}
