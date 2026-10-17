package com.ryandens.jlink

import com.ryandens.jlink.tasks.JlinkJreTask
import org.gradle.api.file.Directory
import org.gradle.jvm.toolchain.JavaCompiler
import org.gradle.jvm.toolchain.JavaInstallationMetadata
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class JlinkJreModulePathTest {
    @field:TempDir
    lateinit var projectDir: File

    @Test fun `defaults to packaged modules on JDK 25`() {
        val jmods = projectDir.resolve("jdk/jmods").apply { mkdirs() }
        val task = createTask(25)

        assertEquals(
            jmods.canonicalFile,
            task.modulePath
                .get()
                .asFile.canonicalFile,
        )
    }

    @Test fun `leaves module path unset for a JDK without packaged modules`() {
        val task = createTask(25)

        assertFalse(task.modulePath.isPresent)
    }

    @Test fun `explicit module path overrides packaged modules`() {
        projectDir.resolve("jdk/jmods").mkdirs()
        val task = createTask(25)
        val customPath = projectDir.resolve("custom-modules").apply { mkdirs() }
        task.modulePath.set(customPath)

        assertEquals(
            customPath.canonicalFile,
            task.modulePath
                .get()
                .asFile.canonicalFile,
        )
    }

    @Test fun `default module path follows a different task toolchain`() {
        projectDir.resolve("jdk/jmods").mkdirs()
        val task = createTask(25)
        val project = task.project
        val otherInstallation = project.layout.projectDirectory.dir("other-jdk")
        otherInstallation.dir("jmods").asFile.mkdirs()
        task.javaCompiler.set(compiler(otherInstallation, 25))

        assertEquals(otherInstallation.dir("jmods"), task.modulePath.get())
    }

    @Test fun `defaults to packaged modules on JDK 17`() {
        val jmods = projectDir.resolve("jdk/jmods").apply { mkdirs() }
        val task = createTask(17)

        assertEquals(
            jmods.canonicalFile,
            task.modulePath
                .get()
                .asFile.canonicalFile,
        )
    }

    private fun createTask(javaVersion: Int): JlinkJreTask {
        val project = ProjectBuilder.builder().withProjectDir(projectDir).build()
        project.plugins.apply("com.ryandens.jlink-jre")
        val installationPath = project.layout.projectDirectory.dir("jdk")
        val task = project.tasks.getByName("jlinkJre") as JlinkJreTask
        task.javaCompiler.set(compiler(installationPath, javaVersion))
        return task
    }

    private fun compiler(
        installationPath: Directory,
        javaVersion: Int,
    ): JavaCompiler =
        object : JavaCompiler {
            override fun getExecutablePath() = installationPath.file("bin/javac")

            override fun getMetadata() =
                object : JavaInstallationMetadata {
                    override fun getLanguageVersion() = JavaLanguageVersion.of(javaVersion)

                    override fun getJavaRuntimeVersion() = "$javaVersion"

                    override fun getJvmVersion() = "$javaVersion"

                    override fun getVendor() = "test"

                    override fun getInstallationPath(): Directory = installationPath

                    override fun isCurrentJvm() = false
                }
        }
}
