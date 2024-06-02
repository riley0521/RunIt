plugins {
    alias(libs.plugins.runtracker.android.library.plugin)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rfdotech.core.connectivity.data"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.connectivity.domain)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.google.coroutines)
    implementation(libs.play.services.wearable)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.bundles.koin)
}