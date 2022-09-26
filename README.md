# jlink Gradle plugin

[![Verify Build](https://github.com/ryandens/jlink-gradle-plugin/actions/workflows/build-verification.yaml/badge.svg)](https://github.com/ryandens/jlink-gradle-plugin/actions/workflows/build-verification.yaml)
[![jlink Application Plugin](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/com.ryandens/jlink-gradle/maven-metadata.xml.svg?label=Jlink%20Application%20Plugin)](https://plugins.gradle.org/plugin/com.ryandens.jlink-application)
[![jlink Jib Plugin](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/com.ryandens/jlink-jib/maven-metadata.xml.svg?label=Jlink%20Jib%20Plugin)](https://plugins.gradle.org/plugin/com.ryandens.jlink-jib)


This repository defines a set of Gradle plugins that enable the use of [jlink](TODO) in Gradle builds. In general,
this plugin strives to enable developers to run tasks with jlink-created JREs or build distributions with embedded jlink
JREs. Generally, this plugin uses the provided java toolchain to locate a valid `jlink` executable and uses that to 
create a minimal java runtime for your application. This enables smaller, more secure, application distributions. 

## Jlink Application plugin usage

This Gradle plugin tightly integrates with the [Gradle application plugin](https://docs.gradle.org/current/userguide/application_plugin.html)
to make running applications via Gradle with jlink-created JREs easy and modifying distributions created by the application
plugin to automatically use a jlink JRE rather than a user provided JRE.

This plugin applies the `com.ryandens.jlink-application-run`, which configures the `run` task to launch the java process
using the java launcher created by the `com.ryandens.jlink-jre`. In addition, it applies the 
`com.ryandens.jlink-application-distribution` plugin which modifies the main distribution to include the custom runtime
built by `com.ryandens.jlink-jre`. In addition, it replaces the application start script with one that uses the built-in
jlink java binary rather than the java binary discovered relative on the host.

```kotlin
plugins {
  application
  id("com.ryandens.jlink-application") version "0.1.0"
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
  modules.set(setOf("java.sql", "java.instrument")) // defaults to only java.base
}

```

## Jlink Jib plugin usage

This Gradle plugin tightly integrates with the [the [jib-gradle-plugin](https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin)
via the [jib-extensions](https://github.com/GoogleContainerTools/jib-extensions) API to make building container images 
via Gradle with jlink-created JREs easy. This plugin replaces the specified base image of your container image with
[distroless/java-base](https://console.cloud.google.com/gcr/images/distroless/global/java-base), which has no JRE
installed. It will also add the JRE created by `jlink-jre` plugin as a separate layer of your container image and modify
the entrypoint of your application to use the `java` executable from the custom JRE rather than expecting one to be on
the path. You must provide an image sha256 for the `java-base` container image you'd like to use.


```kotlin
plugins {
  application
  id("com.ryandens.jlink-jib") version "0.1.0"
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
  }
}

application {
  mainClass.set("yourMainClass")
}

jlinkJib {
  javaBaseSha.set("4682dc38e7658f2c9de5d41df0a9b4e1472f376b82724e332bea91de33a83fbf")
}

jlinkJre {
  modules.set(setOf("java.sql", "java.instrument")) // defaults to only java.base
}

```

This enables two key advantages over traditional distroless images built by jib:
- Smaller image sizes. For a simple application previously based on the java 17 distorless image, I saw a reduction in size by 170 MB
- Enable the use of distroless images with modern java runtimes. Typically, the Google distroless project only creates container images for LTS releases of Java as defined by Oracle. 

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
  modules.set(setOf("java.sql", "java.instrument")) // defaults to only java.base
}

// by default, the project toolchain is used, but this can be overridden at the task level
tasks.named<JlinkJreTask>(JlinkJrePlugin.JLINK_JRE_TASK_NAME) {
  javaCompiler.set(javaToolchains.compilerFor {
    languageVersion.set(JavaLanguageVersion.of(18))
  })
}

// the created runtime can be leveraged in other tasks by accessing its custom java launcher
tasks.withType<JavaExec> {
  javaLauncher.set(tasks.named<JlinkJreTask>(JlinkJrePlugin.JLINK_JRE_TASK_NAME).flatMap { it.javaLauncher })
}

// additional runtimes can be created and used in the same fashion
val jre11 = tasks.register<JlinkJreTask>("createJre11") {
  modules.set(listOf("java.base"))
  javaCompiler.set(javaToolchains.compilerFor {
    languageVersion.set(JavaLanguageVersion.of(11))
  })
}

tasks.register<JavaExec>("runOn11") {
  javaLauncher.set(jre11.flatMap { it.javaLauncher })
  classpath = sourceSets["main"].runtimeClasspath
  mainClass.set("yourMainClass")
}

```
