import com.android.build.api.dsl.LibraryExtension
import com.rfdotech.convention.ExtensionType
import com.rfdotech.convention.addAndroidTestDependencies
import com.rfdotech.convention.addTestDependencies
import com.rfdotech.convention.configureAndroidFirebase
import com.rfdotech.convention.configureBuildTypes
import com.rfdotech.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                configureAndroidFirebase(this)
                configureBuildTypes(this, ExtensionType.LIBRARY)

                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
                }
            }

            dependencies {
                "testImplementation"(kotlin("test"))

                addTestDependencies(target)
                addAndroidTestDependencies(target)
            }
        }
    }
}