plugins {
    alias(libs.plugins.runtracker.android.library.plugin)
}

android {
    namespace = "com.rfdotech.auth.data"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.auth.domain)
}