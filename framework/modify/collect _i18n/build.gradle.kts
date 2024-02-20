plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    compileOnly(gradleApi())
    testImplementation(kotlin("test"))
}

gradlePlugin {
    plugins {
        create("modify-code") {
            id = "idv.neo.plugin.modify.collect.i18n.keys"
            implementationClass = "ModifyCollecti18nPlugin"
        }
    }
}
