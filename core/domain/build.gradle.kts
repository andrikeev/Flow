plugins {
    id("flow.android.library")
}

android {
    namespace = "flow.domain"
}

dependencies {
    implementation(project(":core:auth:api"))
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:dispatchers"))
    implementation(project(":core:downloads"))
    implementation(project(":core:logger"))
    implementation(project(":core:models"))
    implementation(project(":core:network:api"))
    implementation(project(":core:notifications"))
    implementation(project(":core:work:api"))

    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.core)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += listOf(
        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
    )
}
