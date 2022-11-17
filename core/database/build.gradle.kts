plugins {
    id("flow.android.library")
    id("flow.android.hilt")
}

android {
    namespace = "flow.database"
}

dependencies {
    implementation(project(":core:models"))

    implementation(libs.bundles.room)

    kapt(libs.room.compiler)
}
