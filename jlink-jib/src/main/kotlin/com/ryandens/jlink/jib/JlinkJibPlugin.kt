package com.ryandens.jlink.jib

import com.google.cloud.tools.jib.api.buildplan.AbsoluteUnixPath
import com.google.cloud.tools.jib.api.buildplan.ContainerBuildPlan
import com.google.cloud.tools.jib.api.buildplan.FileEntriesLayer
import com.google.cloud.tools.jib.api.buildplan.FileEntry
import com.google.cloud.tools.jib.api.buildplan.FilePermissions
import com.google.cloud.tools.jib.api.buildplan.LayerObject
import com.google.cloud.tools.jib.gradle.JibExtension
import com.google.cloud.tools.jib.gradle.extension.GradleData
import com.google.cloud.tools.jib.gradle.extension.JibGradlePluginExtension
import com.google.cloud.tools.jib.plugins.extension.ExtensionLogger
import com.ryandens.jlink.JlinkJrePlugin
import com.ryandens.jlink.tasks.JlinkJreTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.nio.file.attribute.PosixFilePermission
import java.util.Optional

class JlinkJibPlugin : JibGradlePluginExtension<Void>, Plugin<Project> {

    override fun getExtraConfigType(): Optional<Class<Void>> {
        return Optional.empty()
    }

    override fun extendContainerBuildPlan(
        buildPlan: ContainerBuildPlan?,
        properties: MutableMap<String, String>?,
        extraConfig: Optional<Void>?,
        gradleData: GradleData?,
        logger: ExtensionLogger?,
    ): ContainerBuildPlan {
        checkNotNull(buildPlan)
        val entrypoint = checkNotNull(buildPlan.entrypoint)

        val project = gradleData!!.project
        val jlinkJreOutput = project.tasks.named(JlinkJrePlugin.JLINK_JRE_TASK_NAME, JlinkJreTask::class.java).get().outputDirectory
        val jreInstallationDirectory = "/usr/lib/jvm/jlink-jre/"
        val planBuilder = buildPlan.toBuilder()

        // set base image to java-base, which doesn't have java installed on it
        planBuilder.setBaseImage("gcr.io/distroless/java-base@sha256:${project.extensions.getByType(JlinkJibPluginExtension::class.java).javaBaseSha.get()}")

        // create entrypoint, replace java executable with our own executable
        entrypoint.removeFirst()
        entrypoint.add(0, "${jreInstallationDirectory}jre/bin/java")
        planBuilder.setEntrypoint(entrypoint.toList())

        // create jlink layer
        val entries = jlinkJreOutput.get().asFileTree.files.map {
            FileEntry(it.toPath(), AbsoluteUnixPath.get("$jreInstallationDirectory${it.toRelativeString(jlinkJreOutput.get().asFile)}"), FilePermissions.fromPosixFilePermissions(setOf(PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_READ)), FileEntriesLayer.DEFAULT_MODIFICATION_TIME)
        }.toMutableList()
        val jlinkLayer = FileEntriesLayer.builder().setName("jlink").setEntries(entries).build()
        val layers = mutableListOf<LayerObject>()
        layers.addAll(buildPlan.layers)
        layers.add(1, jlinkLayer)
        return planBuilder.setLayers(layers.toList()).build()
    }

    override fun apply(project: Project) {
        project.pluginManager.apply(JlinkJrePlugin::class.java)

        project.extensions.create(JlinkJibPluginExtension.NAME, JlinkJibPluginExtension::class.java)

        val jreTask = project.tasks.named(JlinkJrePlugin.JLINK_JRE_TASK_NAME, JlinkJreTask::class.java)

        listOf("jib", "jibDockerBuild", "jibBuildTar").forEach { jibTaskName ->
            project.tasks.named(jibTaskName) { jibTask ->
                jibTask.inputs.dir(jreTask.get().outputDirectory)
            }
        }

        // configure the jib extension
        val jibExtension: JibExtension? = project.extensions.findByType(JibExtension::class.java)
        jibExtension?.pluginExtensions { extensionParametersSpec ->
            extensionParametersSpec.pluginExtension {
                it.implementation = "com.ryandens.jlink.jib.JlinkJibPlugin"
            }
        }
    }
}
