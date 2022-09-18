package com.ryandens.jlink

import org.gradle.api.provider.ListProperty

abstract class JlinkJreExtension {

  companion object {
    const val NAME = "jlinkJre"
  }

  abstract val modules: ListProperty<String>
}