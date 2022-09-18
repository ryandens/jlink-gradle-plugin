package com.ryandens.jlink

import com.ryandens.jlink.tasks.JlinkJreTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

class JlinkJrePlugin : Plugin<Project> {

  companion object {
    const val JLINK_JRE_TASK_NAME = "jlinkJre"
  }

  override fun apply(project: Project) {
    project.pluginManager.apply(JavaPlugin::class.java)
    val extension = project.extensions.create(JlinkJreExtension.NAME, JlinkJreExtension::class.java).apply {
      modules.convention(listOf("java.base"))
    }

    project.tasks.register(JLINK_JRE_TASK_NAME, JlinkJreTask::class.java) {
      it.modules.set(extension.modules)
    }
  }
}