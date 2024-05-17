import com.rfdotech.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureUiFirebaseConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.apply("runtracker.android.feature.ui")

            dependencies {
                "implementation"(platform(libs.findLibrary("firebase.bom").get()))
            }
        }
    }
}