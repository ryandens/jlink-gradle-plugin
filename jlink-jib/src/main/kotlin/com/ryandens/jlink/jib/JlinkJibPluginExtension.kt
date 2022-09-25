package com.ryandens.jlink.jib

import org.gradle.api.provider.Property

abstract class JlinkJibPluginExtension {

    companion object {
        const val NAME = "jlinkJib"
    }

    abstract val javaBaseSha: Property<String>
}
