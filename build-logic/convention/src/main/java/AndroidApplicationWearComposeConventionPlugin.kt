import com.android.build.api.dsl.ApplicationExtension
import com.rfdotech.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationWearComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.apply("runtracker.android.application.compose")

            val extension = extensions.getByType<ApplicationExtension>()
            extension.apply {
                defaultConfig {
                    minSdk = libs.findVersion("wearMinSdkVersion").get().toString().toInt()
                }
            }

            dependencies {
                "implementation"(libs.findBundle("compose.wear").get())
                "implementation"(libs.findLibrary("play.services.wearable").get())
            }
        }
    }
}