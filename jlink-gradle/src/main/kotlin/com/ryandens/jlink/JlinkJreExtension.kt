package com.ryandens.jlink

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

abstract class JlinkJreExtension {
    companion object {
        const val NAME = "jlinkJre"
    }

    abstract val modules: ListProperty<String>

    @Deprecated(
        message = "The 'compress' property is deprecated. Use the new compression configuration instead. To be removed in September 2026",
        replaceWith = ReplaceWith("zipCompress"),
    )
    abstract val compress: Property<Int>

    abstract val zipCompress: Property<Compress>

    abstract val stripDebug: Property<Boolean>

    abstract val noHeaderFiles: Property<Boolean>

    abstract val noManPages: Property<Boolean>

    abstract val endian: Property<Endian>

    enum class Endian {
        LITTLE,
        BIG,
        NATIVE,
    }

    enum class Compress {
        ZIP_0,
        ZIP_1,
        ZIP_2,
        ZIP_3,
        ZIP_4,
        ZIP_5,
        ZIP_6,
        ZIP_7,
        ZIP_8,
        ZIP_9,
    }
}
