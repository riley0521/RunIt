plugins {
    alias(libs.plugins.runtracker.android.library.plugin)
}

android {
    namespace = "com.rfdotech.core.test_util"
}

dependencies {
    implementation(projects.core.domain)

    implementation(libs.bundles.testing)
    implementation(libs.bundles.android.testing)
}