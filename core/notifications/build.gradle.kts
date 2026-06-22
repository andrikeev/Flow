plugins {
    id("flow.android.library")
}

android {
    namespace = "flow.notifications"
}

dependencies {
    implementation(project(":core:models"))
    implementation(project(":core:designsystem"))
}
