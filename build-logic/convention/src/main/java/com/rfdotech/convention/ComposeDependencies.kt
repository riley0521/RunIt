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

    addTestDependencies(this)
    addAndroidTestDependencies(this)
}

internal fun DependencyHandlerScope.addAndroidTestDependencies(target: Project) = with(target) {
    "androidTestImplementation"(libs.findLibrary("io.mockk.android").get())
    "androidTestImplementation"(libs.findLibrary("kotlin.coroutines.test").get())
    "androidTestImplementation"(libs.findLibrary("app.cash.turbine").get())
    "androidTestImplementation"(libs.findLibrary("willowtreeapps.assertk").get())
    "androidTestImplementation"(libs.findLibrary("androidx.test.ext.junit").get())
    "androidTestImplementation"(libs.findLibrary("androidx.test.espresso.core").get())
    "androidTestImplementation"(libs.findLibrary("androidx.test.espresso.intents").get())
    "androidTestImplementation"(libs.findLibrary("androidx.test.runner").get())
    "androidTestImplementation"(libs.findLibrary("androidx.test.rules").get())
}

internal fun DependencyHandlerScope.addTestDependencies(target: Project) = with(target) {
    "testImplementation"(libs.findLibrary("io.mockk").get())
    "testImplementation"(libs.findLibrary("willowtreeapps.assertk").get())
    "testImplementation"(libs.findLibrary("kotlin.coroutines.test").get())
    "testImplementation"(libs.findLibrary("app.cash.turbine").get())
}