/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Gradle plugin project to get you started.
 * For more details take a look at the Writing Custom Plugins chapter in the Gradle
 * User Manual available at https://docs.gradle.org/7.5.1/userguide/custom_plugins.html
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    // Apply the Java Gradle plugin development plugin to add support for developing Gradle plugins
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.20.0"
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.6.21"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

description = """
  A set of Gradle plugins for leveraging the jlink tool provided by the JDK and integrating it 
  with built-in Gradle tasks and plugins. 
""".trimIndent()

group = "com.ryandens"
version = "0.1.0"

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest()
        }

        // Create a new test suite
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

gradlePlugin {
    // Define the plugin
    val jlinkJre by plugins.creating {
        id = "com.ryandens.jlink-jre"
        implementationClass = "com.ryandens.jlink.JlinkJrePlugin"
    }
    val jlinkApplicationRun by plugins.creating {
      id = "com.ryandens.jlink-application-run"
      implementationClass = "com.ryandens.jlink.JlinkJreApplicationRunPlugin"
    }
    val jlinkApplicationDistribution by plugins.creating {
      id = "com.ryandens.jlink-application-distribution"
      implementationClass = "com.ryandens.jlink.JlinkJreApplicationDistributionPlugin"
    }
    val jlinkApplication by plugins.creating {
      id = "com.ryandens.jlink-application"
      implementationClass = "com.ryandens.jlink.JlinkJreApplicationPlugin"
    }
}

gradlePlugin.testSourceSets(sourceSets["functionalTest"])

tasks.named<Task>("check") {
    // Include functionalTest as part of the check lifecycle
    dependsOn(testing.suites.named("functionalTest"))
}
