plugins {
    alias(libs.plugins.runtracker.android.library.plugin)
}

android {
    namespace = "com.rfdotech.core.notification"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.presentation.designsystem)
    implementation(projects.core.presentation.ui)

    implementation(libs.androidx.core.ktx)
    implementation(libs.bundles.koin)
}