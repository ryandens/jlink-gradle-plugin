package com.ryandens.jlink

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.configurationcache.extensions.capitalized
import java.util.regex.Pattern
import javax.inject.Inject

abstract class JlinkJreExtension @Inject constructor(objects: ObjectFactory) {

    companion object {
        const val NAME = "jlinkJre"
    }

    val variants: NamedDomainObjectContainer<JlinkVariant> = objects.domainObjectContainer(JlinkVariant::class.java) { name ->
        objects.newInstance(JlinkVariant::class.java, name).apply {
            endian.convention(this@JlinkJreExtension.endian)
        }
    }

    abstract val modules: ListProperty<String>

    abstract val compress: Property<Int>

    abstract val stripDebug: Property<Boolean>

    abstract val noHeaderFiles: Property<Boolean>

    abstract val noManPages: Property<Boolean>

    abstract val endian: Property<Endian>

    fun variants(action: Action<NamedDomainObjectContainer<JlinkVariant>>) {
        action.execute(variants)
    }

    enum class Endian {
        LITTLE,
        BIG,
        NATIVE,
    }
}

abstract class JlinkVariant @Inject constructor(val name: String) {
    /**
     * JDK version this variant should use.
     *
     * Ex. `17.0.5`
     */
    abstract val version: Property<String>

    /**
     * Endianness for this variant. Defaults to [JlinkJreExtension.endian].
     */
    abstract val endian: Property<JlinkJreExtension.Endian>

    /**
     * Operating system this variant targets.
     *
     * See [KnownOperatingSystem] and [CustomOperatingSystem].
     */
    abstract val operatingSystem: Property<OperatingSystem>

    /**
     * Coordinates at which to find the JDK this variant targets.
     *
     * This property does not typically need to be set, but can be provided to customize logic.
     */
    abstract val jdkDependencyCoordinates: Property<String>

    val jlinkTaskName get() = "jlink${name.capitalized()}"

    init {
        jdkDependencyCoordinates.convention(
            version.map { variantVersion ->
                val majorVersion = Pattern.compile("^\\D*(\\d+)").matcher(variantVersion).run {
                    find()
                    group(1)
                }
                val embeddedVersion = variantVersion.trimStart { !it.isDigit() }.replace('+', '_')
                "temurin$majorVersion-binaries:OpenJDK${majorVersion}U-jdk_${name}_hotspot_$embeddedVersion:$variantVersion@${operatingSystem.get().archiveExtension}"
            },
        )
    }
}
