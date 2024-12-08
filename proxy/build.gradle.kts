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

kotlin {
    compilerOptions {
        optIn.addAll(
            "kotlin.io.encoding.ExperimentalEncodingApi",
        )
    }
}

dependencies {
    implementation(libs.bundles.ktor)
    implementation(libs.jsoup)
    implementation(libs.koin)
    implementation(project(":core:network:rutracker"))
}
