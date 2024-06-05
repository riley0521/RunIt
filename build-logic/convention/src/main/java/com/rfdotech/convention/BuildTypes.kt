package com.rfdotech.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DynamicFeatureExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureBuildTypes(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    extensionType: ExtensionType
) {
    commonExtension.run {
        buildFeatures {
            buildConfig = true
        }

        val apiKey = gradleLocalProperties(rootDir, rootProject.providers).getProperty("API_KEY")
        val authApiKey = gradleLocalProperties(rootDir, rootProject.providers).getProperty("AUTH_API_KEY")
        val password = gradleLocalProperties(rootDir, rootProject.providers).getProperty("KEYSTORE_PASS")

        signingConfigs {
            create("release") {
                keyAlias = "runit"
                storeFile = file("C:\\Users\\riley\\Documents\\runit_keystore.jks")
                keyPassword = password
                storePassword = password
            }
        }

        when (extensionType) {
            ExtensionType.APPLICATION -> {
                extensions.configure<ApplicationExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType(apiKey, authApiKey)
                        }
                        release {
                            signingConfig = signingConfigs.getByName("release")
                            configureReleaseBuildType(commonExtension, apiKey, authApiKey)
                        }
                    }
                }
            }
            ExtensionType.LIBRARY -> {
                extensions.configure<LibraryExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType(apiKey, authApiKey)
                        }
                        release {
                            configureReleaseBuildType(commonExtension, apiKey, authApiKey)
                        }
                    }
                }
            }
            ExtensionType.DYNAMIC_FEATURE -> {
                extensions.configure<DynamicFeatureExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType(apiKey, authApiKey)
                        }
                        release {
                            configureReleaseBuildType(commonExtension, apiKey, authApiKey, isDynamicFeature = true)
                        }
                    }
                }
            }
        }
    }
}

private fun BuildType.configureDebugBuildType(apiKey: String, authApiKey: String) {
    buildConfigField("String", "AUTH_API_KEY", "\"$authApiKey\"")
    buildConfigField("String", "API_KEY", "\"$apiKey\"")
    buildConfigField("String", "BASE_URL", "\"https://runique.pl-coding.com:8080\"")
}

private fun BuildType.configureReleaseBuildType(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    apiKey: String,
    authApiKey: String,
    isDynamicFeature: Boolean = false
) {
    buildConfigField("String", "AUTH_API_KEY", "\"$authApiKey\"")
    buildConfigField("String", "API_KEY", "\"$apiKey\"")
    buildConfigField("String", "BASE_URL", "\"https://runique.pl-coding.com:8080\"")

    isMinifyEnabled = !isDynamicFeature
    if (!isDynamicFeature) {
        proguardFiles(
            commonExtension.getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}