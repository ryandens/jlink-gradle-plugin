import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    alias(libs.plugins.spotless)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.compileJava {
    options.release.set(17)
}

tasks.compileKotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
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
    implementation(libs.gradle.compatibility.plugin)
}
