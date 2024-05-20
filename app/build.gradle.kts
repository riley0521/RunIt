plugins {
    alias(libs.plugins.runtracker.android.application.compose.plugin)
    alias(libs.plugins.runtracker.jvm.ktor.plugin)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.rfdotech.runtracker"

    defaultConfig {

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    dynamicFeatures += setOf(":analytics:analytics_feature")
}

dependencies {

    // Local modules
    implementation(projects.core.presentation.designsystem)
    implementation(projects.core.presentation.ui)
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.core.database)
    implementation(projects.core.testUtil)

    implementation(projects.auth.presentation)
    implementation(projects.auth.domain)
    implementation(projects.auth.data)

    implementation(projects.run.presentation)
    implementation(projects.run.domain)
    implementation(projects.run.data)
    implementation(projects.run.location)
    implementation(projects.run.network)

    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Coil
    implementation(libs.io.coil.compose)

    // Google
    api(libs.google.core)
    implementation(libs.google.play.services.location)

    // Timber Logger
    implementation(libs.com.jakewharton.timber)

    // Splash Screen API
    implementation(libs.androidx.core.splashscreen)

    // Crypto for Security
    implementation(libs.androidx.security.crypto.ktx)

    // Koin
    implementation(libs.bundles.koin)

    // Jetpack Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.bundles.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}