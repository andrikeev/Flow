@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Flow"

include(":app")
include(":core:auth")
include(":core:common")
include(":core:data")
include(":core:dispatchers")
include(":core:database")
include(":core:designsystem")
include(":core:domain")
include(":core:downloads")
include(":core:logger")
include(":core:models")
include(":core:network")
include(":core:networkutils")
include(":core:notifications")
include(":core:securestorage")
include(":core:testing")
include(":core:ui")
include(":core:work")

include(":feature:account")
include(":feature:forum")
include(":feature:login")
include(":feature:menu")
include(":feature:search")
include(":feature:topic")
include(":feature:topics")
