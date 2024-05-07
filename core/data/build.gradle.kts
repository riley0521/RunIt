plugins {
    alias(libs.plugins.runtracker.android.library.plugin)
    alias(libs.plugins.runtracker.jvm.ktor.plugin)
}

android {
    namespace = "com.rfdotech.core.data"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.database)

    implementation(libs.bundles.koin)
    implementation(libs.com.jakewharton.timber)
}