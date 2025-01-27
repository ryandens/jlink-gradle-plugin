package com.ryandens.jlink

import com.ryandens.jlink.tasks.JlinkJreTask
import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * A simple unit test for the 'com.ryandens.jlink-jre' plugin.
 */
class JlinkJrePluginTest {
    @Test fun `plugin registers task`() {
        // Create a test project and apply the plugin
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("com.ryandens.jlink-jre")

        // Verify the result
        val jlinkJreTask = project.tasks.getByName("jlinkJre") as JlinkJreTask
        assertNotNull(jlinkJreTask)
        assertEquals(
            "jmods",
            jlinkJreTask.modulePath
                .get()
                .asFile.name,
        )
    }
}
