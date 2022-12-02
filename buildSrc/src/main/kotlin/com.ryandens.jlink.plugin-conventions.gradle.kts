plugins {
    `java-gradle-plugin`
    id("org.jetbrains.kotlin.jvm")
    `maven-publish`
    id("com.gradle.plugin-publish")
    id("com.diffplug.spotless")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(18))
    }
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

pluginBundle {
    website = "https://www.ryandens.com"
    vcsUrl = "https://github.com/ryandens/javaagent-gradle-plugin"
    tags = listOf("jlink", "docker", "jib", "application")
}

group = "com.ryandens"
version = "0.1.0"

spotless {
    kotlin {
        ktlint()
    }
    kotlinGradle {
        ktlint()
    }
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest()
        }

        // Create a new test suite, unused variable is needed for registration
        @Suppress("UNUSED_VARIABLE")
        val functionalTest by registering(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest()

            dependencies {
                // functionalTest test suite depends on the production code in tests
                implementation(project)
            }

            targets {
                all {
                    // This test suite should run after the built-in test suite has run its tests
                    testTask.configure { shouldRunAfter(test) }
                }
            }
        }
    }
}

gradlePlugin.testSourceSets(sourceSets["functionalTest"])

tasks.named<Task>("check") {
    // Include functionalTest as part of the check lifecycle
    dependsOn(testing.suites.named("functionalTest"))
}
