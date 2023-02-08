plugins {
    id("flow.ktor.application")
}

group = "flow.proxy.rutracker"
version = "3.1.0"

application {
    mainClass.set("flow.proxy.rutracker.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

tasks.jar {
    manifest { attributes["Main-Class"] = application.mainClass }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    configurations.compileClasspath.get().forEach { file: File ->
        when {
            file.isFile -> from(zipTree(file.absoluteFile))
            file.isDirectory -> from(fileTree(file.absoluteFile))
        }
    }
}

dependencies {
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.ktor.server)
    implementation(libs.jsoup)
    implementation(libs.koin)
    implementation(libs.logback.classic)
    implementation(project(":core:network:rutracker"))
}
