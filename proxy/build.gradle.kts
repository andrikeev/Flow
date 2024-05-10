import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id("flow.ktor.application")
}

group = "flow.proxy.rutracker"
version = "3.1.0"

application {
    mainClass.set("flow.proxy.rutracker.ApplicationKt")
}

ktor {
    fatJar {
        archiveFileName.set("app.jar")
    }
}

tasks.named<KotlinCompilationTask<*>>("compileKotlin").configure {
    compilerOptions.freeCompilerArgs.add("-opt-in=kotlin.io.encoding.ExperimentalEncodingApi")
}

dependencies {
    implementation(libs.bundles.ktor)
    implementation(libs.jsoup)
    implementation(libs.koin)
    implementation(libs.logback.classic)
    implementation(project(":core:network:rutracker"))
}
