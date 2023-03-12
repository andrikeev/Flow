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
include(":proxy")
include(":core:auth:api")
include(":core:auth:impl")
include(":core:common")
include(":core:data")
include(":core:dispatchers")
include(":core:database")
include(":core:designsystem")
include(":core:domain")
include(":core:downloads")
include(":core:logger")
include(":core:models")
include(":core:network:api")
include(":core:network:impl")
include(":core:network:proxy")
include(":core:network:rutracker")
include(":core:notifications")
include(":core:securestorage")
include(":core:testing")
include(":core:ui")
include(":core:work:api")
include(":core:work:impl")

include(":feature:account")
include(":feature:bookmarks")
include(":feature:category")
include(":feature:favorites")
include(":feature:forum")
include(":feature:login")
include(":feature:menu")
include(":feature:search")
include(":feature:search_input")
include(":feature:search_result")
include(":feature:topic")
include(":feature:visited")
