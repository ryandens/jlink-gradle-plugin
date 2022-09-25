plugins {
  id("com.ryandens.jlink.plugin-conventions")
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

pluginBundle {
  website = "https://www.ryandens.com"
  vcsUrl = "https://github.com/ryandens/javaagent-gradle-plugin"
  tags = listOf("javaagent", "instrumentation", "docker", "jib", "application")
}


description = """
  A gradle plugin and jib extension enabling the automatic use of a jlink runtime in a container image built by jib.
""".trimIndent()

group = "com.ryandens"
version = "0.1.0"

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation(project(":jlink-gradle"))
  implementation("com.google.cloud.tools:jib-gradle-plugin-extension-api:0.4.0")
  implementation("com.google.cloud.tools:jib-gradle-plugin:3.3.0")
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
  val jlinkJib by plugins.creating {
    id = "com.ryandens.jlink-jib"
    implementationClass = "com.ryandens.jlink.jib.JlinkJibPlugin"
  }
}

gradlePlugin.testSourceSets(sourceSets["functionalTest"])

tasks.named<Task>("check") {
  // Include functionalTest as part of the check lifecycle
  dependsOn(testing.suites.named("functionalTest"))
}
