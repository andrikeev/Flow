plugins {
    id("flow.android.feature")
    id("flow.android.library.compose")
}

android {
    namespace = "flow.menu"
}

dependencies {
    implementation(project(":feature:account"))
    implementation(project(":feature:connection"))
}
