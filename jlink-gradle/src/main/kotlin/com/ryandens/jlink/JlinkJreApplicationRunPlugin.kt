package com.ryandens.jlink

import com.ryandens.jlink.tasks.JlinkJreTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.tasks.JavaExec

class JlinkJreApplicationRunPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(ApplicationPlugin::class.java)
        project.pluginManager.apply(JlinkJrePlugin::class.java)
        project.tasks.named(ApplicationPlugin.TASK_RUN_NAME, JavaExec::class.java) {
            val jlinkJreTask = project.tasks.named(JlinkJrePlugin.JLINK_JRE_TASK_NAME, JlinkJreTask::class.java).get()
            val jlinkOutput = jlinkJreTask.outputDirectory
            it.inputs.dir(jlinkOutput)
            it.javaLauncher.set(
                JlinkJavaLauncher(
                    jlinkJreTask.javaCompiler.map { javaCompiler ->
                        javaCompiler.metadata
                    },
                    jlinkJreTask.outputDirectory.file("jre/bin/java"),
                ),
            )
        }
    }
}
