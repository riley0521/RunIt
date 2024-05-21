plugins {
    alias(libs.plugins.runtracker.android.feature.ui.plugin)
}

android {
    namespace = "com.rfdotech.auth.presentation"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.auth.domain)

    implementation(libs.bundles.koin)
    implementation(libs.androidx.activity.compose)
}