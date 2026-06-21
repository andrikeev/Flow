plugins {
    id("flow.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:models"))
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
