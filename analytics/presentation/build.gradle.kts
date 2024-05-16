plugins {
    alias(libs.plugins.runtracker.android.feature.ui.plugin)
}

android {
    namespace = "com.rfdotech.analytics.presentation"
}

dependencies {
    implementation(projects.analytics.domain)
    implementation(projects.core.domain)
}