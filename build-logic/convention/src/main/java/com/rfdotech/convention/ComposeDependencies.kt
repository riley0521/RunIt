package com.rfdotech.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.project

internal fun DependencyHandlerScope.addUiLayerDependencies(target: Project) = with(target) {
    "implementation"(project(":core:presentation:designsystem"))
    "implementation"(project(":core:presentation:ui"))

    "implementation"(libs.findBundle("koin.compose").get())
    "implementation"(libs.findBundle("compose").get())
    "debugImplementation"(libs.findBundle("compose.debug").get())
    "androidTestImplementation"(libs.findLibrary("androidx.compose.ui.test.junit4").get())
    "androidTestImplementation"(libs.findLibrary("androidx.navigation.testing").get())
}

internal fun DependencyHandlerScope.addAndroidTestDependencies(target: Project) = with(target) {
    "androidTestImplementation"(libs.findBundle("android.testing").get())
}

internal fun DependencyHandlerScope.addTestDependencies(target: Project) = with(target) {
    "testImplementation"(libs.findBundle("testing").get())
}