plugins {
  application
  id("org.beryx.runtime") version "1.12.5"
}

application {
  mainClass.set("com.ryandens.example.App")
}

runtime {
  modules.set(setOf("java.base" ))
}