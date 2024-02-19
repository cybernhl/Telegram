import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.support.kotlinCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

@Suppress("UnstableApiUsage")
fun Project.configureAndroid() {
    extensions.configure<BaseExtension> {
        buildToolsVersion = "34.0.0"
        ndkVersion = "21.4.7075529"

        defaultConfig {
            minSdk = 19
            targetSdk = 34
            externalNativeBuild {
                cmake {
                    version = "3.27.7"
                    abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a"))
                }
            }
        }

        compileOptions {
            // https://developer.android.com/studio/write/java8-support
            isCoreLibraryDesugaringEnabled = true
        }

        buildTypes {
            maybeCreate("debug")
            maybeCreate("HA_private")
            maybeCreate("HA_public")
            maybeCreate("HA_hardcore")
            maybeCreate("standalone")
            maybeCreate("release")
        }

        flavorDimensions("minApi","base")
        productFlavors {
            create("bundleAfat") {
                dimension("minApi")
            }
            create("bundleAfat_SDK23"){
                dimension("minApi")
            }
            create("afat"){
                dimension("minApi")
            }
            create("staging"){//test
                dimension("base")
            }
            create("dev") {
                dimension("base")
                setIsDefault(true)
            }
            create("press"){
                dimension("base")
            }
            create("prod"){
                dimension("base")
            }
        }
    }

    extensions.configure<AndroidComponentsExtension<*, *, *>>("androidComponents") {
        beforeVariants { variant -> //Here filter
            variant.enable = FLAVOR_FILTER_LIST.contains(variant.flavorName to variant.buildType)
        }
    }
}

private val FLAVOR_FILTER_LIST = listOf(
    "bundleAfatStaging" to "debug",
    "bundleAfatDev" to "debug",
    "bundleAfatPress" to "debug",
    "bundleAfatProd" to "debug",

    "bundleAfat_SDK23Staging" to "debug",
    "bundleAfat_SDK23Dev" to "debug",
    "bundleAfat_SDK23Press" to "debug",
    "bundleAfat_SDK23Prod" to "debug",

    "afatStaging" to "debug",
    "afatDev" to "debug",
    "afatPress" to "debug",
    "afatProd" to "debug",

    "bundleAfatDev" to "HA_public",
    "bundleAfat_SDK23Dev" to "HA_private",
    "afatDev" to "release",
)
