plugins {
    alias(libs.plugins.runtracker.android.application.wear.compose)
}

android {
    namespace = "com.rfdotech.wear.app"
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.bundles.compose)
    implementation(libs.bundles.koin.compose)
    debugImplementation(libs.bundles.compose.debug)
}