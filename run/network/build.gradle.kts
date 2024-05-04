plugins {
    alias(libs.plugins.runtracker.android.library.plugin)
    alias(libs.plugins.runtracker.jvm.ktor.plugin)
}

android {
    namespace = "com.rfdotech.run.network"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.data)
}