package com.rfdotech.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroidFirebase(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    commonExtension.run {
        dependencies {
            "implementation"(platform(libs.findLibrary("firebase.bom").get()))
        }
    }
}