plugins {
    id("flow.android.library")
    id("flow.android.hilt")
    id("flow.kotlin.ksp")
    id("androidx.room")
}

android {
    namespace = "flow.database"

    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    implementation(project(":core:models"))

    implementation(libs.bundles.room)

    ksp(libs.room.compiler)
}
