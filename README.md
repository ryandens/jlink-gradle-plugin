# jlink Gradle plugin

This repository defines a set of Gradle plugins that enable the use of [jlink](TODO) in Gradle builds. In general,
this plugin strives to enable developers to run tasks with jlink-created JREs or build distributions with embedded jlink
JREs.

## Jlink Application plugin usage

This Gradle plugin tightly integrates with the [Gradle application plugin](https://docs.gradle.org/current/userguide/application_plugin.html)
to make running applications via Gradle with jlink-created JREs easy and modifying distributions created by the application
plugin to automatically use a jlink JRE rather than a user provided JRE.

```kotlin
plugins {
  application
  id("com.ryandens.jlink-application-run") version "0.1.0"
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
  }
}

application {
  mainClass.set("yourMainClass")
}

jlinkJre {
  modules.set(setOf("java.sql", "java.instrument"))
}

```

## Creating a custom runtime

If all you want to do is create a custom runtime specific to an application, simply apply the `jlink-jre` plugin and 
specify the modules you would like in your runtime. Note that this requires the java plugin still as we need to know 
which java toolchain to use for accessing the jlink binary. Simply run the `jlinkJre` task to create the JRE in the 
build directory of the project.

```kotlin
plugins {
  java
  id("com.ryandens.jlink-jre") version "0.1.0"
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
  }
}

jlinkJre {
  modules.set(setOf("java.sql", "java.instrument"))
}

```
