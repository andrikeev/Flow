plugins {
    id("flow.android.library")
    id("flow.android.hilt")
}

android {
    namespace = "flow.notifications"
}

dependencies {
    implementation(project(":core:models"))
    implementation(project(":core:ui"))
}
