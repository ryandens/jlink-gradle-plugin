package com.ryandens.jlink

import com.ryandens.jlink.tasks.JlinkJreTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.distribution.DistributionContainer
import org.gradle.api.distribution.plugins.DistributionPlugin
import org.gradle.api.internal.plugins.WindowsStartScriptGenerator
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.tasks.application.CreateStartScripts

class JlinkJreApplicationDistributionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(ApplicationPlugin::class.java)
        project.pluginManager.apply(JlinkJrePlugin::class.java)

        val jlinkJreOutput =
            project.tasks.named(
                JlinkJrePlugin.JLINK_JRE_TASK_NAME,
                JlinkJreTask::class.java,
            ).get().outputDirectory.dir("jre")

        project.extensions.getByType(DistributionContainer::class.java).named(DistributionPlugin.MAIN_DISTRIBUTION_NAME)
            .configure { distribution ->
                distribution.contents { copy ->
                    copy.from(jlinkJreOutput) {
                        it.into("jre")
                    }
                }
            }

        project.tasks.named(ApplicationPlugin.TASK_START_SCRIPTS_NAME, CreateStartScripts::class.java) {
            // custom start script generator that replaces the JAVACMD with one that maps to the jlink JRE java binary
            it.unixStartScriptGenerator = JlinkAwareStartScriptGenerator()
            // TODO build support for windows
            it.windowsStartScriptGenerator = WindowsStartScriptGenerator()
        }
    }
}
