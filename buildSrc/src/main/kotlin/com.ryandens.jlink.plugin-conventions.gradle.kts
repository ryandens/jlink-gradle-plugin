plugins {
  `java-gradle-plugin`
  id("org.jetbrains.kotlin.jvm")
  `maven-publish`
  id("com.gradle.plugin-publish")
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}
