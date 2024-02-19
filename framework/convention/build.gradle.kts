plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    compileOnly(gradleApi())
    compileOnly(libs.gradlePlugin.android)
    compileOnly(libs.gradlePlugin.kotlin)
}

gradlePlugin {
    plugins {
        create("android-application-convention") {
            id = "idv.neo.plugin.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        create("android-library-convention") {
            id = "idv.neo.plugin.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
    }
}
