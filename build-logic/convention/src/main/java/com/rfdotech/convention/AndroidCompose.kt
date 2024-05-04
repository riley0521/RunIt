package com.rfdotech.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *>
) {
    commonExtension.run {
        buildFeatures {
            compose = true
        }
        composeOptions {
            kotlinCompilerExtensionVersion = libs.findVersion("compose.compiler").get().toString()
        }

        dependencies {
            val bom = libs.findLibrary("androidx.compose.bom").get()
            "implementation"(platform(bom))
            "androidTestImplementation"(platform(bom))
            "debugImplementation"(libs.findLibrary("androidx.compose.ui.tooling.preview").get())

//            "implementation"(libs.findLibrary("androidx.activity.compose").get())
//            "implementation"(libs.findLibrary("androidx.compose.ui").get())
//            "implementation"(libs.findLibrary("androidx.compose.ui.graphics").get())
//            "implementation"(libs.findLibrary("androidx.compose.ui.tooling").get())
//            "implementation"(libs.findLibrary("androidx.compose.material3").get())
//            "implementation"(libs.findLibrary("androidx.compose.ui.tooling.preview").get())
//            "implementation"(libs.findLibrary("androidx.compose.ui.test.manifest").get())
//            "implementation"(libs.findLibrary("androidx.compose.ui.test.junit4").get())
//            "implementation"(libs.findLibrary("androidx.lifecycle.runtime.compose").get())
//            "implementation"(libs.findLibrary("androidx.lifecycle.viewmodel.compose").get())
//            "implementation"(libs.findLibrary("androidx.compose.material.icons.extended").get())
//            "implementation"(libs.findLibrary("androidx.navigation.compose").get())
        }
    }
}