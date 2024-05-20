plugins {
    alias(libs.plugins.runtracker.android.feature.ui.plugin)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets)
}

android {
    namespace = "com.rfdotech.run.presentation"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.testUtil)
    implementation(projects.run.domain)

    implementation(libs.io.coil.compose)
    implementation(libs.google.maps.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.com.jakewharton.timber)
}