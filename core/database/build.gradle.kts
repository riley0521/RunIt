plugins {
    alias(libs.plugins.runtracker.android.library.plugin)
    alias(libs.plugins.runtracker.android.room.plugin)
}

android {
    namespace = "com.rfdotech.core.database"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.testUtil)

    implementation(libs.mongodb.bson)
    implementation(libs.bundles.koin)
}