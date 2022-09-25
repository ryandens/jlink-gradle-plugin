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
  implementation(project(":jlink-gradle"))
  implementation("com.google.cloud.tools:jib-gradle-plugin-extension-api:0.4.0")
  implementation("com.google.cloud.tools:jib-gradle-plugin:3.3.0")
}



gradlePlugin {
  // Define the plugin
  val jlinkJib by plugins.creating {
    id = "com.ryandens.jlink-jib"
    implementationClass = "com.ryandens.jlink.jib.JlinkJibPlugin"
  }
}