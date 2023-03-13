package com.ryandens.jlink

import com.ryandens.jlink.tasks.JlinkJreTask
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.distribution.Distribution
import org.gradle.api.distribution.DistributionContainer
import org.gradle.api.distribution.plugins.DistributionPlugin
import org.gradle.api.file.Directory
import org.gradle.api.internal.plugins.WindowsStartScriptGenerator
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.application.CreateStartScripts

class JlinkJreApplicationDistributionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(ApplicationPlugin::class.java)
        project.pluginManager.apply(JlinkJrePlugin::class.java)

        val distributions = project.extensions.getByType(DistributionContainer::class.java)
        val mainDistribution = distributions.named(DistributionPlugin.MAIN_DISTRIBUTION_NAME)
        val mainDistributionContents = mainDistribution.map(Distribution::getContents)

        fun jlinkTaskOutputs(taskName: String): Provider<Directory> =
            project.tasks.named(taskName, JlinkJreTask::class.java).flatMap { it.outputDirectory.dir("jre") }

        fun configureDistribution(jlinkOutput: Provider<Directory>) = Action<Distribution> { distribution ->
            distribution.contents { copy ->
                copy.from(jlinkOutput) {
                    it.into("jre")
                }
                if (distribution.name != DistributionPlugin.MAIN_DISTRIBUTION_NAME) {
                    copy.with(mainDistributionContents.get())
                        .exclude { it.file.absolutePath.contains("jlink-jre/jre") }
                }
            }
        }

        val jlinkJreOutput = jlinkTaskOutputs(JlinkJrePlugin.JLINK_JRE_TASK_NAME)
        mainDistribution.configure(configureDistribution(jlinkJreOutput))

        project.extensions.getByType(JlinkJreExtension::class.java).variants.all {
            distributions.register(it.name, configureDistribution(jlinkTaskOutputs(it.jlinkTaskName)))
        }

        project.tasks.named(ApplicationPlugin.TASK_START_SCRIPTS_NAME, CreateStartScripts::class.java) {
            // custom start script generator that replaces the JAVACMD with one that maps to the jlink JRE java binary
            it.unixStartScriptGenerator = JlinkAwareStartScriptGenerator()
            // TODO build support for windows
            it.windowsStartScriptGenerator = WindowsStartScriptGenerator()
        }
    }
}
