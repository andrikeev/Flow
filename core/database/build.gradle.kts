plugins {
    id("flow.android.library")
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
    // Target DI for the KMP graph. On Android DAOs are bridged into Hilt (see :app).
    implementation(libs.koin)

    ksp(libs.room.compiler)
}
