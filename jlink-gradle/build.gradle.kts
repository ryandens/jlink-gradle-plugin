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
        displayName = "jlink application jre plugin"
        description = "Creates a minimal java runtime using jlink"
    }
    val jlinkApplicationRun by plugins.creating {
        id = "com.ryandens.jlink-application-run"
        implementationClass = "com.ryandens.jlink.JlinkJreApplicationRunPlugin"
        displayName = "jlink application run plugin"
        description = "Configures the run task to use the jlink java binary as its java launcher"
    }
    val jlinkApplicationDistribution by plugins.creating {
        id = "com.ryandens.jlink-application-distribution"
        implementationClass = "com.ryandens.jlink.JlinkJreApplicationDistributionPlugin"
        displayName = "jlink application distribution plugin"
        description = "Modifies application distribution to use a minimal java runtime built with jlink."
    }
    val jlinkApplication by plugins.creating {
        id = "com.ryandens.jlink-application"
        implementationClass = "com.ryandens.jlink.JlinkJreApplicationPlugin"
        displayName = "jlink application plugin"
        description = "Modifies application plugin to integrate with jlink"
    }
}
