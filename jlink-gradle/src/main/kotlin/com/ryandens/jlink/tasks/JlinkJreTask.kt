package com.ryandens.jlink.tasks

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.AbstractExecTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.jvm.toolchain.JavaCompiler
import org.gradle.jvm.toolchain.JavaToolchainService
import javax.inject.Inject

abstract class JlinkJreTask : AbstractExecTask<JlinkJreTask> {
    @get:Nested
    abstract val javaCompiler: Property<JavaCompiler>

    @get:Input
    abstract val modules: ListProperty<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @Inject
    constructor() : super(JlinkJreTask::class.java) {
        // Access the default toolchain
        val toolchain = project.extensions.getByType(JavaPluginExtension::class.java).toolchain

        // acquire a provider that returns the launcher for the toolchain
        val service = project.extensions.getByType(JavaToolchainService::class.java)
        val defaultJlinkTool = service.compilerFor(toolchain)

        // use it as our default for the property
        javaCompiler.convention(defaultJlinkTool)
        modules.convention(listOf("java.base"))

        outputDirectory.convention(project.layout.buildDirectory.dir("jlink-jre"))
    }

    override fun exec() {
        setExecutable(javaCompiler.get().metadata.installationPath.file("bin/jlink"))
        val jlinkOutput = outputDirectory.dir("jre").get().asFile
        jlinkOutput.deleteRecursively() // jlink expects the output directory to not exist when it runs
        setArgs(listOf("--module-path", javaCompiler.get().metadata.installationPath.dir("jmods").asFile.absolutePath, "--add-modules", modules.get().joinToString(","), "--output", jlinkOutput.absolutePath))
        super.exec()
    }
}
