plugins {
  `java-gradle-plugin`
  id("org.jetbrains.kotlin.jvm")
  `maven-publish`
  id("com.gradle.plugin-publish")
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

testing {
  suites {
    // Configure the built-in test suite
    val test by getting(JvmTestSuite::class) {
      // Use Kotlin Test test framework
      useKotlinTest()
    }

    // Create a new test suite, unused variable is needed for registration
    @Suppress("UNUSED_VARIABLE") val functionalTest by registering(JvmTestSuite::class) {
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

