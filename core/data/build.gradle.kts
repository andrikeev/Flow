plugins {
    id("flow.android.library")
    id("flow.android.hilt")
}

android {
    namespace = "flow.data"
}

dependencies {
    implementation(project(":core:auth:api"))
    implementation(project(":core:database"))
    implementation(project(":core:models"))
    implementation(project(":core:network:api"))
    implementation(project(":core:securestorage"))
}
