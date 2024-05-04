plugins {
    alias(libs.plugins.runtracker.android.library.plugin)
}

android {
    namespace = "com.rfdotech.core.data"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.database)

    implementation(libs.com.jakewharton.timber)
}