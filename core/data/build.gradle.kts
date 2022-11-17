plugins {
    id("flow.android.library")
    id("flow.android.hilt")
}

android {
    namespace = "flow.data"
}

dependencies {
    implementation(project(":core:database"))
    implementation(project(":core:models"))
    implementation(project(":core:network"))
    implementation(project(":core:securestorage"))
    implementation(project(":core:work"))
}
