plugins {
    alias(libs.plugins.runtracker.android.library.compose.plugin)
}

android {
    namespace = "com.rfdotech.core.presentation.ui"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.presentation.designsystem)

    implementation(libs.bundles.compose)
    implementation(libs.bundles.koin)
    implementation(libs.bundles.google.auth)
}