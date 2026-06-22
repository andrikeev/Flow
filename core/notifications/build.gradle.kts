plugins {
    id("flow.android.library")
}

android {
    namespace = "flow.notifications"
}

dependencies {
    implementation(project(":core:models"))
    implementation(project(":core:designsystem"))

    // androidx.core (NotificationCompat etc.) was previously transitive via hilt-android.
    implementation(libs.androidx.core.ktx)
    implementation(libs.koin)
}
