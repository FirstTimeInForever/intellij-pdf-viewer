import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

plugins {
  id("java-library")
  kotlin("jvm")
  id("org.jetbrains.intellij.platform.module")
  id("rpc")
  kotlin("plugin.serialization")
}

repositories {
  mavenCentral()
  intellijPlatform {
    defaultRepositories()
    maven("https://www.jetbrains.com/intellij-repository/snapshots")
  }
}

dependencies {
  intellijPlatform {
    intellijIdea(project.findProperty("platformVersion").toString())
    bundledModule("intellij.platform.rpc")
  }
  api(project(":mpi"))
  api(project(":model"))
  api("io.sentry:sentry:8.44.0") {
    exclude("org.slf4j")
    exclude("com.fasterxml.jackson.core", "jackson-core")
  }
  compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
  api("org.jetbrains.kotlinx:kotlinx-serialization-json:${project.findProperty("kotlinxSerializationJsonVersion")}")
}

intellijPlatform {
  pluginConfiguration {
    id = "com.firsttimeinforever.intellij.pdf.viewer.common"
  }
}
