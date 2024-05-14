plugins {
    alias(libs.plugins.runtracker.android.application.compose.plugin)
    alias(libs.plugins.runtracker.jvm.ktor.plugin)
}

android {
    namespace = "com.rfdotech.runtracker"

    defaultConfig {

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // Local modules
    implementation(projects.core.presentation.designsystem)
    implementation(projects.core.presentation.ui)
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.core.database)

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
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}