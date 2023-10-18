package com.ryandens.jlink

import com.ryandens.jlink.tasks.JlinkJreTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Usage
import org.gradle.api.plugins.JavaPlugin

class JlinkJrePlugin : Plugin<Project> {
    companion object {
        const val JLINK_JRE_TASK_NAME = "jlinkJre"
    }

    override fun apply(project: Project) {
        project.pluginManager.apply(JavaPlugin::class.java)
        val extension =
            project.extensions.create(JlinkJreExtension.NAME, JlinkJreExtension::class.java).apply {
                modules.convention(listOf("java.base"))
                compress.convention(2)
                stripDebug.convention(true)
                noHeaderFiles.convention(true)
                noManPages.convention(true)
                endian.convention(JlinkJreExtension.Endian.NATIVE)
            }

        val jlinkJreTask = project.tasks.register(JLINK_JRE_TASK_NAME, JlinkJreTask::class.java)

        project.tasks.withType(JlinkJreTask::class.java).configureEach {
            it.modules.set(extension.modules)
            it.compress.set(extension.compress)
            it.stripDebug.set(extension.stripDebug)
            it.noHeaderFiles.set(extension.noHeaderFiles)
            it.noManPages.set(extension.noManPages)
            it.endian.set(extension.endian)
        }

        project.configurations.create("jlinkJre") {
            it.isCanBeConsumed = true
            it.isCanBeResolved = false
            it.isVisible = false
            it.attributes.attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage::class.java, "jlink-jre"))
            it.outgoing.artifact(
                jlinkJreTask.flatMap { jreTask ->
                    jreTask.outputDirectory
                },
            )
        }
    }
}
