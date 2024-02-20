import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class ModifyCollecti18nPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        tasks.register<CollectI18nTask>("collectI18n") {
            group = "i18n"
            description = "Collect i18n codes"

            source(
                childProjects.values.flatMapTo(mutableListOf()) {
                    listOf(
                        file("${it.projectDir}/src/main/java/org/telegram"),
                        file("${it.projectDir}/src/main/kotlin/org/telegram"),
                    )
                }
            )
            include("**/*.java", "**/*.kt")

            langKtFile = provider {
                file("TMessagesProj/src/main/kotlin/org/telegram/messenger/utils/LangMultiExt.kt")
            }
        }

        Unit
    }
}