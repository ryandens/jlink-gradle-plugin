pluginManagement {
    includeBuild("build-logic")
}

plugins {
  id("com.gradle.develocity") version "4.3.2"
  id("com.gradle.common-custom-user-data-gradle-plugin") version "2.4.0"
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