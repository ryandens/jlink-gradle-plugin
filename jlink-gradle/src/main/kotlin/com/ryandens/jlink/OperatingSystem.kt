package com.ryandens.jlink

import org.gradle.api.Project
import org.gradle.api.file.FileTree

sealed interface OperatingSystem {
    /**
     * Path to the `jmods` folder of a JDK.
     */
    val jmodsPath: String

    /**
     * Extension of the archive download containing the JDK.
     */
    val archiveExtension: String

    /**
     * The mechanism by which the JDK archive can be extracted.
     */
    val archiveExtractor: (Project, Any) -> FileTree
}

enum class KnownOperatingSystem(
    override val jmodsPath: String,
    override val archiveExtension: String,
    override val archiveExtractor: (Project, Any) -> FileTree,
) : OperatingSystem {
    WINDOWS("jmods", "zip", Project::zipTree),
    MAC("Contents/Home/jmods", "tar.gz", Project::tarTree),
    LINUX("jmods", "tar.gz", Project::tarTree),
}

class CustomOperatingSystem(
    override val jmodsPath: String,
    override val archiveExtension: String,
    override val archiveExtractor: (Project, Any) -> FileTree,
) : OperatingSystem
