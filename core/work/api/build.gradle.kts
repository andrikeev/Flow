plugins {
    id("flow.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:models"))
        }
    }
}
