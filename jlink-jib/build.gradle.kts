plugins {
    id("com.ryandens.jlink.plugin-conventions")
}

description = """
  A gradle plugin and jib extension enabling the automatic use of a jlink runtime in a container image built by jib.
""".trimIndent()

dependencies {
    implementation(project(":jlink-gradle"))
    implementation("com.google.cloud.tools:jib-gradle-plugin-extension-api:0.4.0")
    implementation("com.google.cloud.tools:jib-gradle-plugin:3.3.0")
    testImplementation("org.apache.commons:commons-compress:1.21")
}

gradlePlugin {
    // Define the plugin
    val jlinkJib by plugins.creating {
        id = "com.ryandens.jlink-jib"
        implementationClass = "com.ryandens.jlink.jib.JlinkJibPlugin"
        displayName = "jlink jib plugin"
    }
}
