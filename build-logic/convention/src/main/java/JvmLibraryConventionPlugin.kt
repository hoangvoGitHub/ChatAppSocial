import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
//                apply("chatappsocial.android.lint")
            }
            extensions.configure<JavaPluginExtension> {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17

                tasks.withType<KotlinCompile>().configureEach {
                    kotlinOptions {
                        // Set JVM target to 17
                        jvmTarget = JavaVersion.VERSION_17.toString()

                        val warningsAsErrors: String? by project
                        allWarningsAsErrors = warningsAsErrors.toBoolean()
                        freeCompilerArgs = freeCompilerArgs + listOf(
                            "-Xcontext-receivers",
                            "-opt-in=kotlin.RequiresOptIn",
                        )
                    }
                }
            }
        }
    }
}