package com.ryandens.jlink

import org.gradle.api.Plugin
import org.gradle.api.Project

class JlinkJreApplicationPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(JlinkJreApplicationRunPlugin::class.java)
        project.pluginManager.apply(JlinkJreApplicationDistributionPlugin::class.java)
    }
}
