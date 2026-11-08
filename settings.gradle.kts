pluginManagement {
    includeBuild("build-logic")
}

plugins {
  id("com.gradle.develocity") version "4.6.1"
  id("com.gradle.common-custom-user-data-gradle-plugin") version "2.8.0"
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    exclusiveContent {
      forRepository { gradlePluginPortal() }
      filter {
        includeGroup("com.google.cloud.tools.jib")
        includeModule("com.google.cloud.tools", "jib-gradle-plugin")
      }
    }
  }
}

rootProject.name = "jlink-gradle-plugin"
include("jlink-gradle")
include("jlink-jib")
include("temurin-binaries-repository")

val isCI = providers.environmentVariable("CI").isPresent

develocity {
  buildScan {
    termsOfUseUrl.set("https://gradle.com/help/legal-terms-of-use")
    termsOfUseAgree.set("yes")
    uploadInBackground.set(isCI)
    if (isCI) {
      publishing {
        onlyIf { true }
      }
    }
  }
}
