plugins {
    alias(libs.plugins.runtracker.android.library.plugin)
}

android {
    namespace = "com.rfdotech.analytics.data"
}

dependencies {
    implementation(projects.analytics.domain)
    implementation(projects.core.domain)
    implementation(projects.core.database)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.bundles.koin)
}