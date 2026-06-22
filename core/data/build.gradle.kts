plugins {
    id("flow.android.library")
}

android {
    namespace = "flow.data"
}

dependencies {
    implementation(project(":core:auth:api"))
    implementation(project(":core:common"))
    implementation(project(":core:database"))
    implementation(project(":core:dispatchers"))
    implementation(project(":core:logger"))
    implementation(project(":core:models"))
    implementation(project(":core:network:api"))
    implementation(project(":core:preferences"))

    // Repositories and services are wired with Koin.
    implementation(libs.koin)
    // Previously transitive via hilt-android.
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.koin.test)
    testImplementation(libs.junit4)
}
