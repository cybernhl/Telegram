import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinTopLevelExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

fun Project.configKotlin() {
    extensions.configure<KotlinTopLevelExtension> {
        jvmToolchain(17)
    }
}