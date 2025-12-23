# jlink Gradle plugin

[![Verify Build](https://github.com/ryandens/jlink-gradle-plugin/actions/workflows/gradle.yml/badge.svg)](https://github.com/ryandens/jlink-gradle-plugin/actions/workflows/gradle.yml)
[![jlink Application Plugin](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/com.ryandens/jlink-gradle/maven-metadata.xml.svg?label=Jlink%20Application%20Plugin)](https://plugins.gradle.org/plugin/com.ryandens.jlink-application)
[![jlink Jib Plugin](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/com.ryandens/jlink-jib/maven-metadata.xml.svg?label=Jlink%20Jib%20Plugin)](https://plugins.gradle.org/plugin/com.ryandens.jlink-jib)


This repository defines a set of Gradle plugins that enable the use of 
[jlink](https://docs.oracle.com/javase/9/tools/jlink.htm) in Gradle builds. In general, this plugin strives to enable 
developers to run tasks with jlink-created JREs or build distributions with embedded jlink JREs. Generally, this plugin 
uses the provided java toolchain to locate a valid `jlink` executable and uses that to create a minimal java runtime for 
your application. This enables smaller, more secure, application distributions. 

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
  id("com.ryandens.jlink-application") version "0.6.0"
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

This Gradle plugin tightly integrates with the [jib-gradle-plugin](https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin)
via the [jib-extensions](https://github.com/GoogleContainerTools/jib-extensions) API to make building container images 
via Gradle with jlink-created JREs easy. This plugin adds the JRE created by `jlink-jre` plugin as a separate layer of 
your container image and modify the entrypoint of your application to use the `java` executable from the custom JRE 
rather than expecting one to be on the path. It is recommended to override the default jib base image with a base image
that does not have java installed, in order to reap the most benefits of jlink. We recommend 
`gcr.io/distroless/java-base-debian11:nonroot-arm64@sha256:YOUR_PREFERRED_SHA_HERE`, replacing `YOUR_PREFERRED_SHA_HERE`
with the latest released image sha.


```kotlin
plugins {
  application
  id("com.google.cloud.tools.jib") version "3.3.1"
  id("com.ryandens.jlink-jib") version "0.6.0"
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}

application {
  mainClass.set("yourMainClass")
}

// this base image does not have a java installation
jib.from.image = "gcr.io/distroless/java-base-debian11:nonroot-arm64"

```

This enables two key advantages over traditional distroless images built by jib:
- Smaller image sizes. For a simple application previously based on the java 17 distorless image, I saw a reduction in size by 90 MB when using the `java-base-debian11:nonroot-arm64` image with a jlink JRE added compared to `java17-debian11:nonroot-arm64`
- Enable the use of distroless images with modern java runtimes. Typically, the Google distroless project only creates container images for LTS releases of Java as defined by Oracle. 


### Jlink Jib plugin usage for different architectures

When building a container image for a different architecture than your host platform, you'll need to configure the jlink
task to use the java modules for your target platform. For example, when building a linux container image on OS X,
the default jlink-jre task will output a JRE for the OS X platform, so the resulting container image built by `jib` 
would not be able to run the OS X native executables. In that case, it is recommended to use the
`temurin-binaries-repository`, as shown below, to resolve the JDK for another platform, register a new `JlinkJreTask` 
task for generating a JRE for the desired platform, and configure the `jlink-jib` plugin to use the new`JlinkJreTask` 
output as the source of the JRE that will be inserted into the container image.

```kotlin
plugins {
  application
  id("com.ryandens.jlink-jib") version "0.6.0"
  id("com.ryandens.temurin-binaries-repository") version "0.6.0"
}

val jdk by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
    isVisible = false
}

dependencies {
    jdk("temurin19-binaries:OpenJDK19U-jdk_aarch64_linux_hotspot_19.0.2_7:jdk-19.0.2+7@tar.gz")
}

val copyJdks = tasks.register<Copy>("copyJdks") {
    from(provider {tarTree(jdk.singleFile)})
    into(project.layout.buildDirectory.dir("jdks"))
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}

application {
  mainClass.set("yourMainClass")
}

// this base image does not have a java installation
jib.from.image = "gcr.io/distroless/java-base-debian11:nonroot-arm64"

val linuxJlinkJre = project.tasks.register<JlinkJreTask>("linuxJlinkJre") {
    // by default, JlinkJreTask uses the module path associated with the configured java toolchain that is executing
    // the jlink command, but this can be overridden to instead point at a different jmods directory for the purpose
    // of building a JRE for a different platform
    this.modulePath.fileProvider(copyJdks.map { File(it.destinationDir, "jdk-19.0.2+7/jmods/") })
    outputDirectory.set(file(layout.buildDirectory.dir("jlink-jre-linux")))
}

jlinkJib {
    // by default, this plugin will include the JRE built by the jlinkJre task. Optionally, any directory can be used.
    // this enables the default jlinkJre task to build a JRE for one platform and a custom JlinkJreTask to build a JRE
    // for another platform
    jlinkJre.value(linuxJlinkJre.map { it.outputDirectory.get() })
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
  id("com.ryandens.jlink-jre") version "0.6.0"
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
