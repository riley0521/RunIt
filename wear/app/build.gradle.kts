plugins {
    alias(libs.plugins.runtracker.android.application.wear.compose)
}

android {
    namespace = "com.rfdotech.wear.app"

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(projects.core.connectivity.data)
    implementation(projects.core.connectivity.domain)
    implementation(projects.core.data)
    implementation(projects.core.notification)
    implementation(projects.core.presentation.designsystemWear)
    implementation(projects.wear.run.data)
    implementation(projects.wear.run.domain)
    implementation(projects.wear.run.presentation)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.com.jakewharton.timber)

    implementation(libs.bundles.compose)
    implementation(libs.bundles.koin.compose)
    debugImplementation(libs.bundles.compose.debug)
}