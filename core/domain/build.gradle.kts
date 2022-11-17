plugins {
    id("flow.android.library")
}

android {
    namespace = "flow.domain"
}

dependencies {
    implementation(project(":core:auth"))
    implementation(project(":core:data"))
    implementation(project(":core:downloads"))
    implementation(project(":core:models"))
    implementation(project(":core:work"))

    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.core)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += listOf(
        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
    )
}
