plugins {
    `kotlin-dsl`
}

group = "com.rfdotech.runtracker.build-logic"

dependencies {
    compileOnly(libs.android.tools.build.gradle)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.google.devtools.ksp.gradle.plugin)
    compileOnly(libs.androidx.room.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "runtracker.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "runtracker.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "runtracker.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "runtracker.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidFeatureUi") {
            id = "runtracker.android.feature-ui"
            implementationClass = "AndroidFeatureUiConventionPlugin"
        }
        register("androidRoom") {
            id = "runtracker.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("androidDynamicFeature") {
            id = "runtracker.android.dynamic_feature"
            implementationClass = "AndroidDynamicFeatureConventionPlugin"
        }
        register("jvmLibrary") {
            id = "runtracker.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("jvmKtor") {
            id = "runtracker.jvm.ktor"
            implementationClass = "JvmKtorConventionPlugin"
        }
    }
}