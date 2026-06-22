plugins {
    id("flow.android.library")
}

android {
    namespace = "flow.downloads"
}

dependencies {
    // androidx.core (getSystemService ktx) was previously transitive via hilt-android.
    implementation(libs.androidx.core.ktx)
}
