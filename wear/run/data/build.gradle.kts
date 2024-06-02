plugins {
    alias(libs.plugins.runtracker.android.library.plugin)
}

android {
    namespace = "com.rfdotech.wear.run.data"

    defaultConfig {
        minSdk = libs.versions.wearMinSdkVersion.get().toString().toInt()
    }
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.wear.run.domain)

    implementation(libs.google.health.services)
    implementation(libs.bundles.koin)
}