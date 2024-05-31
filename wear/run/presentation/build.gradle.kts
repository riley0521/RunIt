plugins {
    alias(libs.plugins.runtracker.android.library.compose.plugin)
}

android {
    namespace = "com.rfdotech.wear.run.presentation"

    defaultConfig {
        minSdk = libs.versions.wearMinSdkVersion.get().toString().toInt()
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.play.services.wearable)

    implementation(libs.bundles.compose.wear)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.koin.compose)
}