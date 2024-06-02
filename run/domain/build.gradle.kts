plugins {
    alias(libs.plugins.runtracker.jvm.library.plugin)
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.connectivity.domain)

    implementation(libs.kotlinx.coroutines.core)
}