plugins {
    `kotlin-dsl`
    id("com.diffplug.spotless") version "6.11.0"
}

repositories {
    gradlePluginPortal()
}

spotless {
    kotlinGradle {
        target("*.gradle.kts", "src/main/kotlin/*.gradle.kts")
        ktlint()
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
    implementation("com.gradle.publish:plugin-publish-plugin:1.0.0")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.11.0")
}
