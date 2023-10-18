package com.ryandens.jlink.tasks

import com.ryandens.jlink.JlinkJreExtension
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.AbstractExecTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
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

    @get:Input
    abstract val compress: Property<Int>

    @get:Input
    abstract val stripDebug: Property<Boolean>

    @get:Input
    abstract val noHeaderFiles: Property<Boolean>

    @get:Input
    abstract val noManPages: Property<Boolean>

    @get:Input
    abstract val endian: Property<JlinkJreExtension.Endian>

    @get:InputDirectory
    abstract val modulePath: DirectoryProperty

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
        modulePath.convention(javaCompiler.map { it.metadata.installationPath.dir("jmods") })

        outputDirectory.convention(project.layout.buildDirectory.dir("jlink-jre"))
    }

    override fun exec() {
        setExecutable(javaCompiler.get().metadata.installationPath.file("bin/jlink"))
        val jlinkOutput = outputDirectory.dir("jre").get().asFile
        jlinkOutput.deleteRecursively() // jlink expects the output directory to not exist when it runs

        args =
            buildList {
                addAll(
                    listOf(
                        "--module-path",
                        modulePath.get().asFile.absolutePath,
                        "--add-modules",
                        modules.get().joinToString(","),
                        "--compress",
                        "${compress.get()}",
                        "--output",
                        jlinkOutput.absolutePath,
                    ),
                )

                if (stripDebug.get()) {
                    add("--strip-debug")
                }

                if (noHeaderFiles.get()) {
                    add("--no-header-files")
                }

                if (noManPages.get()) {
                    add("--no-man-pages")
                }

                if (endian.get() != JlinkJreExtension.Endian.NATIVE) {
                    add("--endian")
                    add(endian.get().toString().lowercase())
                }
            }
        super.exec()
    }
}
