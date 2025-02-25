package com.ryandens.jlink.jib

import org.gradle.internal.impldep.org.apache.commons.compress.archivers.ArchiveInputStream
import org.gradle.internal.impldep.org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.gradle.internal.impldep.org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.FileInputStream
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue

/**
 * A simple functional test for the 'com.ryandens.jlink-application-run' plugin.
 */
class JlinkJibFunctionalTest {
    @field:TempDir
    lateinit var projectDir: File

    private val buildFile by lazy { projectDir.resolve("build.gradle") }
    private val settingsFile by lazy { projectDir.resolve("settings.gradle") }

    @Test fun `can build image with custom jre`() {
        setupProject("\"Hello World\"", "java.base")

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("jibBuildTar")
        runner.withProjectDir(projectDir)
        val result = runner.build()

        val javaExecutableFileName = "java" + if (DefaultNativePlatform.getCurrentOperatingSystem().isWindows) ".exe" else ""

        // Verify the result
        assertTrue(
            File(
                projectDir,
                Paths
                    .get(
                        "build",
                        "jlink-jre",
                        "jre",
                        "bin",
                        javaExecutableFileName,
                    ).toString(),
            ).exists(),
        )
        assertTrue(result.output.contains("Running extension: com.ryandens.jlink.jib.JlinkJibPlugin"))
        assertTrue(File(projectDir, Paths.get("build", "jib-image.tar").toString()).exists())

        FileInputStream(File(projectDir, Paths.get("build", "jib-image.tar").toString())).use { fis ->
            ArchiveStreamFactory().createArchiveInputStream<ArchiveInputStream<TarArchiveEntry>>("tar", fis).use { ais ->
                var entry = ais.nextEntry
                while (entry != null) {
                    if ("config.json" == entry.name) {
                        val json = ais.readBytes().toString(Charsets.UTF_8)
                        // verify entrypoint has been replaced with path to jlink java executable
                        assertContains(json, "/usr/lib/jvm/jlink-jre/jre/bin/java")
                    }
                    entry = ais.nextEntry
                }
            }
        }
    }

    private fun setupProject(
        printlnParam: String,
        module: String,
    ) {
        // Setup the test build
        settingsFile.writeText("")
        buildFile.writeText(
            """
   plugins {
      id('application')
      id('com.google.cloud.tools.jib') version '3.1.4'
      id('com.ryandens.jlink-jib')
   }
  
  application {
    mainClass.set("com.ryandens.example.App")
  }
  
  jlinkJre {
    modules = ['$module']
  }
  
  java {
      toolchain {
          languageVersion = JavaLanguageVersion.of(17)
      }
  }
  """,
        )

        val file = File(projectDir, Paths.get("src", "main", "java", "com", "ryandens", "example").toString())
        file.mkdirs()
        file.resolve("App.java").writeText(
            """
            package com.ryandens.example;
            
            public final class App {
            
              public static void main(final String[] args) {
                System.out.println($printlnParam);
              }
            
            }
            """.trimIndent(),
        )
    }
}
