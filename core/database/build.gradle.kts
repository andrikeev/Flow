plugins {
    id("flow.android.library")
    id("flow.android.hilt")
    id("flow.kotlin.ksp")
}

android {
    namespace = "flow.database"
}

dependencies {
    implementation(project(":core:models"))

    implementation(libs.bundles.room)

    ksp(libs.room.compiler)
}
