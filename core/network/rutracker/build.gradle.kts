plugins {
    id("flow.kmp.library")
}

kotlin {
    sourceSets {
        // Charset/HTTP-specific code (Windows-1251 form encoding via java.net.URLEncoder)
        // is shared by Android and JVM only; native engines are handled in a later stage.
        val nonNativeMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.ktor.client.core)
            }
        }
        androidMain.get().dependsOn(nonNativeMain)
        jvmMain.get().dependsOn(nonNativeMain)

        commonMain.dependencies {
            api(project(":core:network:api"))
            implementation(libs.ksoup)
        }
    }
}
