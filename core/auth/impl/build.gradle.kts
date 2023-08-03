plugins {
    id("flow.android.library")
    id("flow.android.hilt")
}

android {
    namespace = "flow.auth"
}

dependencies {
    api(project(":core:auth:api"))

    implementation(project(":core:common"))
    implementation(project(":core:dispatchers"))
    implementation(project(":core:models"))
    implementation(project(":core:network:api"))
    implementation(project(":core:preferences"))
}
