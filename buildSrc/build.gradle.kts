plugins {
    `kotlin-dsl`
    id("com.diffplug.spotless") version "6.15.0"
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
    implementation("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:1.9.10")
    implementation("com.gradle.plugin-publish:com.gradle.plugin-publish.gradle.plugin:1.2.1")
    implementation("com.diffplug.spotless:com.diffplug.spotless.gradle.plugin:6.22.0")
}
