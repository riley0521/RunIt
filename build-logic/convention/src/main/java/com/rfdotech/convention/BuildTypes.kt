package com.rfdotech.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DynamicFeatureExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.io.File

internal fun Project.configureBuildTypes(
    commonExtension: CommonExtension,
    extensionType: ExtensionType
) {
    commonExtension.buildFeatures.apply { buildConfig = true }

    val apiKey = gradleLocalProperties(rootDir, rootProject.providers).getProperty("API_KEY")
    val authApiKey = gradleLocalProperties(rootDir, rootProject.providers).getProperty("AUTH_API_KEY")
    val password = gradleLocalProperties(rootDir, rootProject.providers).getProperty("KEYSTORE_PASS")

    commonExtension.signingConfigs.apply {
        create("release") {
            keyAlias = "runit"
            storeFile = file("runit_keystore.jks")
            keyPassword = password
            storePassword = password
        }
    }

    when (extensionType) {
        ExtensionType.APPLICATION -> {
            this@configureBuildTypes.extensions.configure<ApplicationExtension> {
                buildTypes {
                    debug {
                        configureDebugBuildType(apiKey, authApiKey)
                    }
                    release {
                        signingConfig = signingConfigs.getByName("release")
                        configureReleaseBuildType(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            apiKey,
                            authApiKey
                        )
                    }
                }
            }
        }
        ExtensionType.LIBRARY -> {
            this@configureBuildTypes.extensions.configure<LibraryExtension> {
                buildTypes {
                    debug {
                        configureDebugBuildType(apiKey, authApiKey)
                    }
                    release {
                        configureReleaseBuildType(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            apiKey,
                            authApiKey
                        )
                    }
                }
            }
        }
        ExtensionType.DYNAMIC_FEATURE -> {
            this@configureBuildTypes.extensions.configure<DynamicFeatureExtension> {
                buildTypes {
                    debug {
                        configureDebugBuildType(apiKey, authApiKey)
                    }
                    release {
                        configureReleaseBuildType(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            apiKey,
                            authApiKey,
                            isDynamicFeature = true
                        )
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
    defaultProguardFile: File,
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
            defaultProguardFile,
            "proguard-rules.pro"
        )
    }
}