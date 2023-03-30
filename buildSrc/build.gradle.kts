plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.kotlin.serializationPlugin)
    implementation(libs.hilt.gradlePlugin)
}

kotlin {
    jvmToolchain(11)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "flow.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "flow.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibrary") {
            id = "flow.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidHilt") {
            id = "flow.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidFeature") {
            id = "flow.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("kotlinLibrary") {
            id = "flow.kotlin.library"
            implementationClass = "KotlinLibraryConventionPlugin"
        }
        register("kotlinSerialization") {
            id = "flow.kotlin.serialization"
            implementationClass = "KotlinSerializationConventionPlugin"
        }
        register("ktorApplication") {
            id = "flow.ktor.application"
            implementationClass = "KtorApplicationConventionPlugin"
        }
    }
}
