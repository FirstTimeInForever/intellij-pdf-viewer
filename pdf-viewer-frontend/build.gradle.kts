import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

plugins {
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
    bundledPlugin("com.intellij.modules.jcef")
    bundledPlugin("intellij.structureView.plugin")
    bundledModule("intellij.platform.frontend")
  }
  api(project(":pdf-viewer-common"))
  implementation(project(":model"))
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}
