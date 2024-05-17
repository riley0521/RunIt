package com.rfdotech.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    commonExtension.run {
        compileSdk = libs.findVersion("projectCompileSdkVersion").get().toString().toInt()
        defaultConfig.minSdk = libs.findVersion("projectMinSdkVersion").get().toString().toInt()

        compileOptions {
            isCoreLibraryDesugaringEnabled = true
        }

        packaging {
            resources {
                excludes += listOf(
                    "/META-INF/{AL2.0,LGPL2.1}",
                    "/META-INF/LICENSE.md",
                    "/META-INF/LICENSE-notice.md"
                )
            }
        }

        extensions.configure<KotlinAndroidProjectExtension> {
            jvmToolchain(17)
        }
    }

    dependencies {
        "coreLibraryDesugaring"(libs.findLibrary("desugar.jdk.libs").get())
    }
}

internal fun Project.configureKotlinJvm() {
    extensions.configure<KotlinJvmProjectExtension> {
        jvmToolchain(17)
    }
}