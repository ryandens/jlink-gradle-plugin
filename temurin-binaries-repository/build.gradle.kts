plugins {
    id("com.ryandens.jlink.plugin-conventions")
}

description = """
  A Gradle plugin that defines an ivy repository for Gradle projects to enable builds to consume binaries released by the Eclipse Adoptium GitHub account
""".trimIndent()

gradlePlugin {
    // Define the plugin
    val temurinBinariesRepository by plugins.creating {
        id = "com.ryandens.temurin-binaries-repository"
        implementationClass = "com.ryandens.temurin.TemurinBinariesRepository"
        displayName = "Temurin binaries repository plugin"
        description = "Defines an ivy repository for Gradle projects to enable builds to consume binaries released by the Eclipse Adoptium GitHub account"
        tags.set(listOf("temurin", "jdk"))
    }
}
