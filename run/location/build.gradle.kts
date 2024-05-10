plugins {
    alias(libs.plugins.runtracker.android.library.plugin)
}

android {
    namespace = "com.rfdotech.run.location"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.run.domain)

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.google.play.services.location)

    implementation(libs.bundles.koin)
}