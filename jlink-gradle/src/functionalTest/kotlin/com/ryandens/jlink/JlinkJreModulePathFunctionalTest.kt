package com.ryandens.jlink

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JlinkJreModulePathFunctionalTest {
    @field:TempDir
    lateinit var projectDir: File

    @Test fun `links ALL-MODULE-PATH from packaged modules`() {
        setupProject(17, "ALL-MODULE-PATH")

        val result = runner().build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":jlinkJre")?.outcome)
        assertRuntimeIncludes("java.sql")
    }

    @Test fun `links named modules on JDK 25 and reuses configuration cache`() {
        setupProject(25, "java.sql")
        projectDir.resolve("build.gradle").appendText(
            "\njlinkJre { zipCompress.set(com.ryandens.jlink.JlinkJreExtension.Compress.ZIP_9) }\n",
        )

        val result = runner().build()
        assertEquals(TaskOutcome.SUCCESS, result.task(":jlinkJre")?.outcome)
        assertRuntimeIncludes("java.sql")

        val secondResult = runner().build()
        assertTrue(secondResult.output.contains("Reusing configuration cache."))
        assertEquals(TaskOutcome.SUCCESS, secondResult.task(":jlinkJre")?.outcome)
        assertRuntimeIncludes("java.sql")
    }

    @Test fun `rejects ALL-MODULE-PATH without a path before deleting the previous image`() {
        setupProject(25, "ALL-MODULE-PATH")
        projectDir.resolve("build.gradle").appendText(
            """

            tasks.named('jlinkJre') {
                modulePath.set(providers.provider { null })
                doFirst {
                    def marker = outputDirectory.file('jre/marker').get().asFile
                    marker.parentFile.mkdirs()
                    marker.text = 'previous image'
                }
            }
            """.trimIndent(),
        )
        val marker = projectDir.resolve("build/jlink-jre/jre/marker")

        val result = runner().buildAndFail()

        assertTrue(result.output.contains("ALL-MODULE-PATH requires an explicit module path"))
        assertTrue(result.output.contains("explicit module names"))
        assertEquals("previous image", marker.readText())
    }

    private fun setupProject(
        javaVersion: Int,
        modules: String,
    ) {
        projectDir.resolve("settings.gradle").writeText("")
        projectDir.resolve("build.gradle").writeText(
            """
            plugins { id 'com.ryandens.jlink-jre' }
            java { toolchain { languageVersion = JavaLanguageVersion.of($javaVersion) } }
            jlinkJre { modules.set(['$modules']) }
            """.trimIndent(),
        )
    }

    private fun runner() =
        GradleRunner
            .create()
            .withPluginClasspath()
            .withProjectDir(projectDir)
            .withArguments("jlinkJre", "--configuration-cache", "--rerun-tasks", "--no-build-cache")
            .forwardOutput()

    private fun assertRuntimeIncludes(module: String) {
        val executable = if (System.getProperty("os.name").startsWith("Windows")) "java.exe" else "java"
        val process =
            ProcessBuilder(projectDir.resolve("build/jlink-jre/jre/bin/$executable").absolutePath, "--list-modules")
                .redirectErrorStream(true)
                .start()
        val output = process.inputStream.bufferedReader().use { it.readText() }
        assertEquals(0, process.waitFor(), output)
        assertTrue(output.lineSequence().any { it.substringBefore('@') == module }, output)
    }
}
