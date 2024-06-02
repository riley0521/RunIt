plugins {
    alias(libs.plugins.runtracker.android.library.compose.plugin)
}

android {
    namespace = "com.rfdotech.core.presentation.designsystem_wear"

    defaultConfig {
        minSdk = libs.versions.wearMinSdkVersion.get().toString().toInt()
    }
}

dependencies {
    api(projects.core.presentation.designsystem)
    implementation(libs.androidx.wear.compose.material3)
}