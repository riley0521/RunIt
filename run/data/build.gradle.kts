plugins {
    alias(libs.plugins.runtracker.android.library.plugin)
}

android {
    namespace = "com.rfdotech.run.data"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.database)
    implementation(projects.run.domain)
    implementation(projects.run.location)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.google.play.services.location)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.io.koin.androidx.workmanager)
    implementation(libs.kotlinx.serialization.json)
}