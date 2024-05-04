plugins {
    `kotlin-dsl`
}

group = "com.rfdotech.runtracker.build-logic"

dependencies {
    compileOnly(libs.android.tools.build.gradle)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.google.devtools.ksp.gradle.plugin)
    compileOnly(libs.androidx.room.gradle.plugin)
}