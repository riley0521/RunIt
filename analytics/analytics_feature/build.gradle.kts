plugins {
    alias(libs.plugins.runtracker.android.dynamic.feature.plugin)
}
android {
    namespace = "com.rfdotech.analytics.analytics_feature"
}

dependencies {
    api(projects.analytics.presentation)
    implementation(projects.analytics.domain)
    implementation(projects.analytics.data)
    implementation(projects.core.database)
    implementation(project(":app"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
}