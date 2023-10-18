package com.ryandens.jlink.jib

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.SetProperty
import java.nio.file.attribute.PosixFilePermission

abstract class JlinkJibPluginExtension {
    companion object {
        const val NAME = "jlinkJib"
    }

    abstract val jrePosixFilePermissions: SetProperty<PosixFilePermission>

    abstract val jlinkJre: DirectoryProperty
}
