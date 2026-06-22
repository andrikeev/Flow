plugins {
    id("flow.android.library")
    id("flow.android.hilt")
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

    // Repositories are wired with Koin; data services remain on Hilt for now.
    implementation(libs.koin)

    testImplementation(libs.koin.test)
    testImplementation(libs.junit4)
}
