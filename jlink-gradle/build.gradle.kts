plugins {
  id("com.ryandens.jlink.plugin-conventions")
}

description = """
  A set of Gradle plugins for leveraging the jlink tool provided by the JDK and integrating it 
  with built-in Gradle tasks and plugins. 
""".trimIndent()

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