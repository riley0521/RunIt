import com.android.build.gradle.LibraryExtension
import com.rfdotech.convention.addUiLayerDependencies
import com.rfdotech.convention.configureAndroidFirebase
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidFeatureUiConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.apply("runtracker.android.library.compose")

            val extension = extensions.getByType<LibraryExtension>()
            configureAndroidFirebase(extension)

            dependencies {
                addUiLayerDependencies(target)
            }
        }
    }
}