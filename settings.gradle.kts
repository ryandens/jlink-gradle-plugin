plugins {
//  id("com.gradle.enterprise") version "3.15.1"
}

rootProject.name = "jlink-gradle-plugin"
include("jlink-gradle")
include("jlink-jib")
include("temurin-binaries-repository")


//val isCI = System.getenv("CI") != null

//gradleEnterprise {
//  buildScan {
//    termsOfServiceUrl = "https://gradle.com/terms-of-service"
//    termsOfServiceAgree = "yes"
//    isUploadInBackground = !isCI
//
//    if (isCI) {
//      publishAlways()
//    }
//
//    capture {
//      isTaskInputFiles = true
//    }
//  }
//}
