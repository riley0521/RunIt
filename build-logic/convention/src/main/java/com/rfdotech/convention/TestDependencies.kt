package com.rfdotech.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope

internal fun DependencyHandlerScope.addAndroidTestDependencies(target: Project) = with(target) {
    "androidTestImplementation"(libs.findBundle("android.testing").get())
}

internal fun DependencyHandlerScope.addTestDependencies(target: Project) = with(target) {
    "testImplementation"(libs.findBundle("testing").get())
}