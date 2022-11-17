plugins {
    id("flow.android.library")
    id("flow.android.hilt")
}

android {
    namespace = "flow.downloads"
}

dependencies {
    implementation(project(":core:auth"))
}
