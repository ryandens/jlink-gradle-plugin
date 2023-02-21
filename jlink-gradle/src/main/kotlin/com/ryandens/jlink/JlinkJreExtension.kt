package com.ryandens.jlink

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

abstract class JlinkJreExtension {

    companion object {
        const val NAME = "jlinkJre"
    }

    abstract val modules: ListProperty<String>

    abstract val compress: Property<Int>

    abstract val stripDebug: Property<Boolean>

    abstract val noHeaderFiles: Property<Boolean>

    abstract val noManPages: Property<Boolean>

    abstract val endian: Property<Endian>

    enum class Endian {
        LITTLE,
        BIG,
        NATIVE,
    }
}
