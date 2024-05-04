plugins {
    alias(libs.plugins.runtracker.android.library.plugin)
}

android {
    namespace = "com.rfdotech.core.database"
}

dependencies {
    implementation(projects.core.domain)

    implementation(libs.mongodb.bson)
}