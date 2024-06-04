pluginManagement {
    includeBuild("build-logic")
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

rootProject.name = "RunIt"

gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:convention:testClasses"))
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")
include(":auth:data")
include(":auth:domain")
include(":auth:presentation")
include(":core:data")
include(":core:database")
include(":core:domain")
include(":core:presentation:designsystem")
include(":core:presentation:designsystem_wear")
include(":core:presentation:ui")
include(":run:data")
include(":run:domain")
include(":run:location")
include(":run:network")
include(":run:presentation")
include(":analytics:data")
include(":analytics:domain")
include(":analytics:presentation")
include(":analytics:analytics_feature")
include(":core:test_util")
include(":wear:app")
include(":wear:run:data")
include(":wear:run:domain")
include(":wear:run:presentation")
include(":core:connectivity:domain")
include(":core:connectivity:data")
include(":core:notification")
