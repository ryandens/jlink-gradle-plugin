package com.ryandens.jlink.jib

import org.gradle.internal.impldep.org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.gradle.internal.impldep.org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.FileInputStream
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * A simple functional test for the 'com.ryandens.jlink-application-run' plugin.
 */
class JlinkJibFunctionalTest {
    @get:Rule val tempFolder = TemporaryFolder()

    private fun getProjectDir() = tempFolder.root
    private fun getBuildFile() = getProjectDir().resolve("build.gradle")
    private fun getSettingsFile() = getProjectDir().resolve("settings.gradle")

    @Test fun `can build image with custom jre`() {
        setupProject("\"Hello World\"", "java.base")

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("jibBuildTar")
        runner.withProjectDir(getProjectDir())
        val result = runner.build()

        // Verify the result
        assertTrue(File(getProjectDir(), "build/jlink-jre/jre/bin/java").exists())
        assertTrue(result.output.contains("Running extension: com.ryandens.jlink.jib.JlinkJibPlugin"))
        assertTrue(File(getProjectDir(), "build/jib-image.tar").exists())

        FileInputStream(File(getProjectDir(), "build/jib-image.tar")).use { fis ->
            ArchiveStreamFactory().createArchiveInputStream("tar", fis).use { ais ->
                var entry = ais.nextEntry as TarArchiveEntry?
                while (entry != null) {
                    if ("config.json" == entry.name) {
                        val json = ais.readBytes().toString(Charsets.UTF_8)
                        // verify entrypoint has been replaced with path to jlink java executable
                        assertTrue(json.contains("/usr/lib/jvm/jlink-jre/jre/bin/java"))
                    }
                    entry = ais.nextEntry as TarArchiveEntry?
                }
            }
        }
    }

    private fun setupProject(printlnParam: String, module: String) {
        // Setup the test build
        getSettingsFile().writeText("")
        getBuildFile().writeText(
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
  
  jlinkJib {
    javaBaseSha = "4682dc38e7658f2c9de5d41df0a9b4e1472f376b82724e332bea91de33a83fbf"
  }
  
  java {
      toolchain {
          languageVersion = JavaLanguageVersion.of(17)
      }
  }
  """
        )

        val file = File(getProjectDir(), "src/main/java/com/ryandens/example/")
        file.mkdirs()
        file.resolve("App.java").writeText(
            """
          package com.ryandens.example;
          
          public final class App {
          
            public static void main(final String[] args) {
              System.out.println($printlnParam);
            }
          
          }
            """.trimIndent()
        )
    }
}
