plugins {
    `kotlin-dsl`
    alias(libs.plugins.spotless)
}

repositories {
    gradlePluginPortal()
}

// Kotlin DSL plugin and jetbrains can't seem to target java 21 for kotlin compilation yet
val jdkBytecodeTargetForBuildLogic = 17
tasks.compileJava {
    options.release.set(jdkBytecodeTargetForBuildLogic)
}

tasks.compileKotlin {
    kotlinOptions {
        // kotlin convert integer into a string
        jvmTarget = "$jdkBytecodeTargetForBuildLogic"
    }
}

spotless {
    kotlinGradle {
        target("*.gradle.kts", "src/main/kotlin/*.gradle.kts")
        ktlint()
    }
}

dependencies {
    implementation(libs.kotlin.jvm)
    implementation(libs.plugin.publish)
    implementation(libs.spotless)
}
