/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package com.ryandens.jlink

import org.gradle.internal.jvm.Jvm
import java.io.File
import kotlin.test.assertTrue
import kotlin.test.Test
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder

/**
 * A simple functional test for the 'com.ryandens.jlink-application-run' plugin.
 */
class JlinkApplicationRunPluginFunctionalTest {
    @get:Rule val tempFolder = TemporaryFolder()

    private fun getProjectDir() = tempFolder.root
    private fun getBuildFile() = getProjectDir().resolve("build.gradle")
    private fun getSettingsFile() = getProjectDir().resolve("settings.gradle")

    @Test fun `can run with custom jre`() {
      setupProject("\"Hello World\"", "java.base")

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("run")
        runner.withProjectDir(getProjectDir())
        val result = runner.build();

        // Verify the result
        assertTrue(File(getProjectDir(), "build/jlink-jre/jre/bin/java").exists())
        assertTrue(result.output.contains("Hello World"))
    }

  @Test fun `build fails when using class from module that is not included`() {
      setupProject("java.sql.Statement.class.getName()", "java.base")
    // Run the build
    val runner = GradleRunner.create()
    runner.forwardOutput()
    runner.withPluginClasspath()
    runner.withArguments("run")
    runner.withProjectDir(getProjectDir())
    val result = runner.buildAndFail();

    // Verify the result
    assertTrue(File(getProjectDir(), "build/jlink-jre/jre/bin/java").exists())
    assertTrue(result.output.contains("java.lang.NoClassDefFoundError: java/sql/Statement"))
  }

  @Test fun `build succeeds when using class from non-default module that is included`() {
    setupProject("java.sql.Statement.class.getName()", "java.sql")
    // Run the build
    val runner = GradleRunner.create()
    runner.forwardOutput()
    runner.withPluginClasspath()
    runner.withArguments("run")
    runner.withProjectDir(getProjectDir())
    val result = runner.build();

    // Verify the result
    assertTrue(File(getProjectDir(), "build/jlink-jre/jre/bin/java").exists())
    assertTrue(result.output.contains("java.sql.Statement"))
  }

  @Test fun `build succeeds for distribution when using class from module that is not included`() {
    setupProject("java.sql.Statement.class.getName()", "java.sql")

    // Run the build
    val runner = GradleRunner.create()
    runner.forwardOutput()
    runner.withPluginClasspath()
    runner.withArguments("installDist", "execStartScript")
    runner.withProjectDir(getProjectDir())
    val result = runner.build();

    // Verify the result
    assertTrue(File(getProjectDir(), "build/install/${getProjectDir().name}/jre/bin/java").exists())
    assertTrue(result.output.contains("java.sql.Statement"))
  }

  @Test fun `build fails for distribution when using class from module that is not included`() {
    setupProject("java.sql.Statement.class.getName()", "java.base")

    // Run the build
    val runner = GradleRunner.create()
    runner.forwardOutput()
    runner.withPluginClasspath()
    runner.withArguments("installDist", "execStartScript")
    runner.withProjectDir(getProjectDir())
    val result = runner.buildAndFail();

    // Verify the result
    assertTrue(File(getProjectDir(), "build/install/${getProjectDir().name}/jre/bin/java").exists())
    assertTrue(result.output.contains("java.lang.NoClassDefFoundError: java/sql/Statement"))
  }

  private fun setupProject(printlnParam: String, module: String) {
    // Setup the test build
    getSettingsFile().writeText("")
    getBuildFile().writeText(
      """
  plugins {
      id('application')
      id('com.ryandens.jlink-application')
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
  
  task execStartScript(type: Exec) {
    workingDir '${getProjectDir().canonicalPath}/build/install/${getProjectDir().name}/bin/'
    commandLine './${getProjectDir().name}'
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
