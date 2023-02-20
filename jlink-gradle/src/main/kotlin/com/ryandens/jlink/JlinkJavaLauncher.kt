package com.ryandens.jlink

import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.jvm.toolchain.JavaInstallationMetadata
import org.gradle.jvm.toolchain.JavaLauncher

class JlinkJavaLauncher(private val metadata: Provider<JavaInstallationMetadata>, private val executablePath: Provider<RegularFile>) : JavaLauncher {
    override fun getMetadata(): JavaInstallationMetadata {
        return metadata.get()
    }

    override fun getExecutablePath(): RegularFile {
        return executablePath.get()
    }
}
